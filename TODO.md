# TODO - CloudCompare AI Spec/Architecture Improvements (Additive)

## Step 1: Add “different chatbots” (UI + backend endpoints)
- [x] Add chat panel UI to `src/main/resources/static/dashboard.html` (Cloud Compare + AI Tools modes)
- [x] Add frontend chat logic in `src/main/resources/static/script.js` (chat mode, send, render)
- [x] Add backend endpoints in `src/main/java/com/cloudcompare/ai/controller/ApiController.java`:
  - [x] `POST /api/chat/cloud`
  - [x] `POST /api/chat/ai-tools`
- [x] Add backend services (additive) that build distinct Groq-backed guidance for the two chatbot modes.

## Step 2: Natural language query support (backward-compatible)
- [x] Update `src/main/java/com/cloudcompare/ai/dto/AiCompareRequest.java` to add optional `queryText`
- [x] Update `POST /api/ai-compare` to use `queryText` if present, otherwise fall back to `purpose`
- [x] Update frontend AI tools panel to optionally accept free-text input (keep dropdown)

## Step 3: Make existing AI Architect summary accessible via chat (extra)
- [x] Add an “Ask Architect” button in dashboard (extra only) that seeds the appropriate chatbot with current results.

## Step 4: Add AWS IaC + optional adapters (no behavior change)
- [x] Create Terraform IaC under `iac/terraform/` for API Gateway + RDS + S3 (additive artifacts only)
- [x] Add AWS integration classes (additive) behind feature flags (env vars) so current MySQL/JPA and mock Groq remain default.

## Step 5: Add/extend tests
- [x] Add controller tests for:
  - [x] `/api/chat/cloud`
  - [x] `/api/chat/ai-tools`
  - [x] `/api/ai-compare` with `queryText`
- [x] Ensure existing tests still pass.

## Step 6: Verification
- [x] Start server for smoke testing.
  - Docker CLI is not available in this environment, so the server was started with `./mvnw spring-boot:run -Dspring-boot.run.profiles=test -Dspring-boot.run.arguments=--server.port=5001`.
- [x] Smoke test:
  - [x] Existing cloud compare dashboard served successfully.
  - [x] Existing AI tools comparison returned successful results.
  - [x] New chatbots returned successful end-to-end responses.
  - [x] NLP free-text input returned successful results.
