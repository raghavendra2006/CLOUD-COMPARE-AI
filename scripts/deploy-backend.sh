#!/usr/bin/env bash
set -euo pipefail

DOCKER_IMAGE="${DOCKER_IMAGE:-raghavendra76/cloudcompare-ai:latest}"
EC2_HOST="${EC2_HOST:-54.92.130.156}"
EC2_USER="${EC2_USER:-ubuntu}"
SSH_KEY="${SSH_KEY:-}"
CONTAINER_NAME="${CONTAINER_NAME:-cloudcompare-ai}"
HOST_PORT="${HOST_PORT:-3000}"
APP_PORT="${APP_PORT:-5000}"

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker is required on this machine to build and push ${DOCKER_IMAGE}." >&2
  exit 1
fi

if [[ -z "${SSH_KEY}" ]]; then
  echo "Set SSH_KEY to your EC2 private key path, for example:" >&2
  echo "  SSH_KEY=~/Downloads/cloudcompare.pem $0" >&2
  exit 1
fi

if [[ ! -f "${SSH_KEY}" ]]; then
  echo "SSH key not found: ${SSH_KEY}" >&2
  exit 1
fi

echo "Building backend jar..."
./mvnw clean package

echo "Building Docker image: ${DOCKER_IMAGE}"
docker build -t "${DOCKER_IMAGE}" .

echo "Pushing Docker image: ${DOCKER_IMAGE}"
docker push "${DOCKER_IMAGE}"

echo "Restarting ${CONTAINER_NAME} on ${EC2_USER}@${EC2_HOST}..."
ssh -i "${SSH_KEY}" -o StrictHostKeyChecking=accept-new "${EC2_USER}@${EC2_HOST}" \
  "DOCKER_IMAGE='${DOCKER_IMAGE}' CONTAINER_NAME='${CONTAINER_NAME}' HOST_PORT='${HOST_PORT}' APP_PORT='${APP_PORT}' bash -s" <<'REMOTE'
set -euo pipefail

ENV_FILE="/tmp/${CONTAINER_NAME}.env"
docker inspect "${CONTAINER_NAME}" --format '{{range .Config.Env}}{{println .}}{{end}}' > "${ENV_FILE}" 2>/dev/null || true

docker pull "${DOCKER_IMAGE}"
docker rm -f "${CONTAINER_NAME}" 2>/dev/null || true

ENV_ARGS=()
if [[ -s "${ENV_FILE}" ]]; then
  ENV_ARGS=(--env-file "${ENV_FILE}")
fi

docker run -d \
  --name "${CONTAINER_NAME}" \
  --restart unless-stopped \
  -p "${HOST_PORT}:${APP_PORT}" \
  "${ENV_ARGS[@]}" \
  "${DOCKER_IMAGE}"

docker ps --filter "name=${CONTAINER_NAME}"
REMOTE

echo "Deployment complete."
echo "Open: http://${EC2_HOST}:${HOST_PORT}/dashboard.html"
