# Run CloudCompare AI via Docker (fallback)

This repo contains only `mvnw.cmd` (Windows wrapper). If Maven isn’t available on your machine, use this Docker-based workflow.

## Prerequisites
- Docker Desktop installed
- Docker Compose available

## 1) Build & start
From repo root:

```bash
docker compose up -d --build
```

If you have Compose v1, use:

```bash
docker-compose up -d --build
```

## 2) Open the dashboard
```text
http://localhost:5000/dashboard.html
```

## Notes
- Backend chat endpoints are not implemented yet; chat messages may fail until `/api/chat/cloud` and `/api/chat/ai-tools` exist.
- Environment variables are configured in `docker-compose.yml`.

## Useful logs
```bash
docker logs -f cloudcompare-ai
```

