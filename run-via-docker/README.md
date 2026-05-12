# Run CloudCompare AI via Docker

Use this Docker-based workflow when you want to run CloudCompare AI without installing Java or Maven locally.

## Prerequisites
- Docker Desktop installed
- Docker Compose available
- Optional Groq API key for live AI responses. Without it, the app can still use fallback behavior where supported.

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
http://localhost:3000/dashboard.html
```

The container maps host port `3000` to application port `5000`.

## Notes
- Backend chat endpoints are available at `/api/chat/cloud` and `/api/chat/ai-tools`.
- Environment variables are configured in `docker-compose.yml`.
- The default application configuration can use an in-memory H2 database for local startup. Set database environment variables when connecting to MySQL or AWS RDS.
- Set `GROK_API_KEYS` to enable Groq/LLaMA-powered recommendation responses.

## Useful logs
```bash
docker logs -f cloudcompare-ai
```

## Stop
```bash
docker compose down
```
