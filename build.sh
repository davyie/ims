#!/usr/bin/env bash
set -euo pipefail

# ─────────────────────────────────────────────
# IMS Build Script
# Builds backend JAR + Docker image, then
# tears down any running containers and
# brings the full stack back up.
# ─────────────────────────────────────────────

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # no colour

log()  { echo -e "${CYAN}[build]${NC} $*"; }
ok()   { echo -e "${GREEN}[ok]${NC}    $*"; }
warn() { echo -e "${YELLOW}[warn]${NC}  $*"; }
die()  { echo -e "${RED}[error]${NC} $*" >&2; exit 1; }

# ── locate project root (the directory this script lives in) ──
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# ── parse flags ──────────────────────────────────────────────
SKIP_TESTS=true
NO_CACHE=false
DOWN_VOLUMES=false

for arg in "$@"; do
  case $arg in
    --with-tests)   SKIP_TESTS=false ;;
    --no-cache)     NO_CACHE=true ;;
    --clean)        DOWN_VOLUMES=true ;;
    --help|-h)
      echo "Usage: ./build.sh [options]"
      echo ""
      echo "Options:"
      echo "  --with-tests   Run Maven tests during build (skipped by default)"
      echo "  --no-cache     Build Docker images with --no-cache"
      echo "  --clean        Run 'docker compose down -v' before starting (wipes DB data)"
      echo "  --help         Show this help"
      exit 0
      ;;
    *) die "Unknown option: $arg  (run ./build.sh --help for usage)" ;;
  esac
done

# ── preflight checks ─────────────────────────────────────────
command -v mvn    >/dev/null 2>&1 || die "Maven (mvn) not found in PATH"
command -v docker >/dev/null 2>&1 || die "Docker not found in PATH"

log "Starting IMS full-stack build…"
echo ""

# ── step 1: Maven build ──────────────────────────────────────
log "Step 1/3 — Building backend with Maven"

MVN_FLAGS="-B --no-transfer-progress"
if $SKIP_TESTS; then
  MVN_FLAGS="$MVN_FLAGS -DskipTests"
  warn "Tests skipped  (pass --with-tests to enable)"
fi

(cd backend && mvn clean install $MVN_FLAGS) \
  || die "Maven build failed"

ok "Maven build complete"
echo ""

# ── step 2: Docker image ─────────────────────────────────────
log "Step 2/3 — Building backend Docker image (application:1.0.0)"

DOCKER_FLAGS=""
if $NO_CACHE; then
  DOCKER_FLAGS="--no-cache"
  warn "Docker cache disabled"
fi

(cd backend && docker build $DOCKER_FLAGS -t application:1.0.0 .) \
  || die "Docker build failed"

ok "Backend image built: application:1.0.0"
echo ""

# ── step 3: Compose up ───────────────────────────────────────
log "Step 3/3 — Starting containers with Docker Compose"

if $DOWN_VOLUMES; then
  warn "--clean specified: stopping containers and wiping volumes"
  docker compose down -v
elif docker compose ps --quiet 2>/dev/null | grep -q .; then
  log "Stopping existing containers"
  docker compose down
fi

# --build rebuilds the frontend image (runs ng build inside Docker)
docker compose up -d --build \
  || die "docker compose up failed"

ok "All containers started"
echo ""

# ── summary ──────────────────────────────────────────────────
echo -e "${GREEN}────────────────────────────────────────${NC}"
echo -e "${GREEN}  Build complete!${NC}"
echo -e "${GREEN}────────────────────────────────────────${NC}"
echo ""
echo "  Frontend   →  http://localhost"
echo "  Backend    →  http://localhost:8080"
echo "  Swagger    →  http://localhost:8080/swagger-ui.html"
echo ""
