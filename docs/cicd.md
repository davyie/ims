# CI/CD Setup

## Overview

The project uses GitHub Actions for CI/CD with three stages:

1. **CI** (`.github/workflows/ci.yml`) — runs on every push/PR
2. **Docker build** (part of CI) — builds and pushes images to GHCR on merges to `main`
3. **Deploy** (`.github/workflows/deploy.yml`) — SSHs into VPS and runs `docker compose` after CI passes on `main`

---

## CI Pipeline (`ci.yml`)

Three jobs run in parallel, with `docker-build` gated on the other two:

```
backend-build ──┐
                ├──▶ docker-build (main only)
frontend-build ─┘
```

### `backend-build`
- Spins up a `postgres:16` service container
- Runs `mvn -B clean verify` with `SPRING_PROFILES_ACTIVE=test`
- Uses the hardcoded test DB credentials (`imsdb_test` / `ims` / `ims`)

### `frontend-build`
- Installs dependencies with `npm ci`
- Runs `ng test --browsers=ChromeHeadlessCI` (uses `karma.conf.js`)
- Runs `ng build --configuration production`

### `docker-build` (main branch only)
- Logs in to GitHub Container Registry (`ghcr.io`) using the automatic `GITHUB_TOKEN`
- Builds and pushes two images:
  - `ghcr.io/<owner>/ims-backend:latest` and `:<sha>`
  - `ghcr.io/<owner>/ims-frontend:latest` and `:<sha>`
- Uses GitHub Actions cache (`type=gha`) to speed up rebuilds

---

## Deploy Pipeline (`deploy.yml`)

Triggers automatically when CI completes successfully on `main`, or manually via `workflow_dispatch`.

**What it does on the VPS:**
1. `git pull origin main` — updates the repo
2. Writes a fresh `.env` from GitHub secrets
3. Logs in to GHCR and runs `docker compose pull`
4. `docker compose up -d --remove-orphans`
5. Health-checks `GET /actuator/health` (10 retries × 5 s)

---

## Required GitHub Secrets

Go to **Settings → Secrets and variables → Actions** and add:

| Secret | Description | How to generate |
|---|---|---|
| `DEPLOY_HOST` | VPS IP or hostname | Your server's IP |
| `DEPLOY_USER` | SSH username | e.g. `ubuntu` |
| `DEPLOY_KEY` | Private SSH key (PEM) | `ssh-keygen -t ed25519` |
| `DB_PASSWORD` | Production Postgres password | Choose a strong password |
| `JWT_SECRET` | Base64-encoded JWT signing key | `openssl rand -base64 64` |

`GITHUB_TOKEN` is provided automatically — no setup needed.

---

## VPS Prerequisites

```bash
# Install Docker
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER

# Clone the repo
git clone https://github.com/<owner>/ims.git ~/ims

# Add the GitHub Actions deploy public key
echo "<public-key>" >> ~/.ssh/authorized_keys
```

---

## GHCR Package Visibility

After the first successful push to `main`, GitHub creates the packages automatically. By default they are private.

To allow the VPS to pull without a PAT:

1. Go to your GitHub profile → **Packages**
2. Open `ims-backend` → **Package settings** → set visibility to **Public**
3. Repeat for `ims-frontend`

Alternatively, create a Personal Access Token (PAT) with `read:packages` scope, store it as `GHCR_TOKEN` secret, and update the docker login step in `deploy.yml`.

---

## Environment Variables Summary

The deploy job writes this `.env` to the VPS at `~/ims/.env`:

| Variable | Source |
|---|---|
| `SPRING_PROFILES_ACTIVE` | hardcoded `prod` |
| `POSTGRES_DB` / `DB_NAME` | hardcoded `imsdb` |
| `POSTGRES_USER` / `DB_USERNAME` | hardcoded `ims` |
| `POSTGRES_PASSWORD` / `DB_PASSWORD` | `secrets.DB_PASSWORD` |
| `JWT_SECRET` | `secrets.JWT_SECRET` |
| `APP_IMAGE` | `ghcr.io/<owner>/ims-backend:latest` |
| `FRONTEND_IMAGE` | `ghcr.io/<owner>/ims-frontend:latest` |
| `APP_PORT` / `FRONTEND_PORT` | `8080` / `80` |
| `API_URL` | `http://application:8080` |

---

## Local Docker Build (manual)

To build and run images locally without CI:

```bash
# Backend
docker build -t application:1.0.0 ./backend

# Frontend
docker build -t frontend:1.0.0 ./frontend/ims-frontend

# Update .env
APP_IMAGE=application:1.0.0
FRONTEND_IMAGE=frontend:1.0.0

# Start the stack
docker compose up -d
```
