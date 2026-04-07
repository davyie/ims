#!/usr/bin/env bash
set -euo pipefail

# ─────────────────────────────────────────────
# IMS Full-Stack Build Script
# Builds all backend microservices + frontend
# and brings the full stack up via Docker Compose.
#
# Usage:
#   ./build.sh                   # standard build (tests skipped)
#   ./build.sh --with-tests      # run Maven tests
#   ./build.sh --no-cache        # Docker build without layer cache
#   ./build.sh --clean           # wipe volumes before starting
#   ./build.sh --skip-maven      # skip Maven pre-build (use Docker cache only)
# ─────────────────────────────────────────────

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

log()  { echo -e "${CYAN}[build]${NC} $*"; }
ok()   { echo -e "${GREEN}[ok]${NC}    $*"; }
warn() { echo -e "${YELLOW}[warn]${NC}  $*"; }
die()  { echo -e "${RED}[error]${NC} $*" >&2; exit 1; }

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# ── parse flags ──────────────────────────────────────────────
SKIP_TESTS=true
NO_CACHE=false
DOWN_VOLUMES=false
SKIP_MAVEN=false

for arg in "$@"; do
  case $arg in
    --with-tests)  SKIP_TESTS=false ;;
    --no-cache)    NO_CACHE=true ;;
    --clean)       DOWN_VOLUMES=true ;;
    --skip-maven)  SKIP_MAVEN=true ;;
    --help|-h)
      echo "Usage: ./build.sh [options]"
      echo ""
      echo "Options:"
      echo "  --with-tests   Run Maven tests during build (skipped by default)"
      echo "  --no-cache     Build Docker images without layer cache"
      echo "  --clean        Wipe all volumes before starting (resets all data)"
      echo "  --skip-maven   Skip Maven pre-build; let Docker handle compilation"
      echo "  --help         Show this help"
      exit 0
      ;;
    *) die "Unknown option: $arg  (run ./build.sh --help for usage)" ;;
  esac
done

# ── preflight checks ─────────────────────────────────────────
command -v docker >/dev/null 2>&1 || die "Docker not found in PATH"

log "Starting IMS full-stack build…"
echo ""

# ── step 1: Maven pre-build (optional but speeds up Docker builds) ────────────
if $SKIP_MAVEN; then
  warn "Skipping Maven pre-build — Docker will compile each service independently"
else
  command -v mvn >/dev/null 2>&1 || die "Maven (mvn) not found in PATH. Use --skip-maven to skip."

  log "Step 1/3 — Building all backend modules with Maven"

  MVN_FLAGS="-B --no-transfer-progress"
  if $SKIP_TESTS; then
    MVN_FLAGS="$MVN_FLAGS -DskipTests"
    warn "Tests skipped (pass --with-tests to enable)"
  fi

  (cd backend && mvn clean install $MVN_FLAGS) \
    || die "Maven build failed"

  ok "Maven build complete"
fi
echo ""

# ── step 2: stop existing containers ─────────────────────────
log "Step 2/3 — Preparing containers"

if $DOWN_VOLUMES; then
  warn "--clean specified: stopping containers and wiping all volumes"
  docker compose down -v 2>/dev/null || true
elif docker compose ps --quiet 2>/dev/null | grep -q .; then
  log "Stopping existing containers"
  docker compose down
fi

# ── step 3: build images and start the full stack ────────────
log "Step 3/3 — Building images and starting all containers"

COMPOSE_FLAGS="--build"
if $NO_CACHE; then
  COMPOSE_FLAGS="$COMPOSE_FLAGS --no-cache"
  warn "Docker cache disabled"
fi

docker compose up -d $COMPOSE_FLAGS \
  || die "docker compose up failed"

ok "All containers started"
echo ""

# ── summary ──────────────────────────────────────────────────
echo -e "${GREEN}────────────────────────────────────────────────────${NC}"
echo -e "${GREEN}  IMS stack is up!${NC}"
echo -e "${GREEN}────────────────────────────────────────────────────${NC}"
echo ""
echo "  Frontend      →  http://localhost"
echo "  API Gateway   →  http://localhost:8080"
echo "  Kafka UI      →  http://localhost:9090"
echo "  Mailhog UI    →  http://localhost:8025"
echo ""
echo "  Individual services:"
echo "    User          →  http://localhost:8081"
echo "    Warehouse     →  http://localhost:8082"
echo "    Market        →  http://localhost:8083"
echo "    Transfer      →  http://localhost:8084"
echo "    Scheduling    →  http://localhost:8085"
echo "    Reporting     →  http://localhost:8086"
echo "    Transaction   →  http://localhost:8087"
echo "    Notification  →  http://localhost:8088"
echo ""
echo "  To follow logs:  docker compose logs -f"
echo "  To stop:         docker compose down"
echo ""
