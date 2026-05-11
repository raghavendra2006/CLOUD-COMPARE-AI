# TODO - CloudCompare AI Spec/Architecture Improvements (Additive)

## Step 1: Add “different chatbots” (UI + backend endpoints)
- [x] Add chat panel UI to `src/main/resources/static/dashboard.html` (Cloud Compare + AI Tools modes)
- [x] Add frontend chat logic in `src/main/resources/static/script.js` (chat mode, send, render)
- [ ] Add backend endpoints in `src/main/java/com/cloudcompare/ai/controller/ApiController.java`:
  - [ ] `POST /api/chat/cloud`
  - [ ] `POST /api/chat/ai-tools`
- [ ] Add backend services (additive) that build distinct Groq prompts for the two chatbot modes.

## Step 2: Natural language query support (backward-compatible)
- [ ] Update `src/main/java/com/cloudcompare/ai/dto/AiCompareRequest.java` to add optional `queryText`
- [ ] Update `POST /api/ai-compare` to use `queryText` if present, otherwise fall back to `purpose`
- [ ] Update frontend AI tools panel to optionally accept free-text input (keep dropdown)

## Step 3: Make existing AI Architect summary accessible via chat (extra)
- [ ] Add an “Ask Architect” button in dashboard (extra only) that seeds the appropriate chatbot with current results.

## Step 4: Add AWS IaC + optional adapters (no behavior change)
- [ ] Create Terraform IaC under `iac/terraform/` for API Gateway + RDS + S3 (additive artifacts only)
- [ ] Add AWS integration classes (additive) behind feature flags (env vars) so current MySQL/JPA and mock Groq remain default.

## Step 5: Add/extend tests
- [ ] Add controller tests for:
  - `/api/chat/cloud`
  - `/api/chat/ai-tools`
  - `/api/ai-compare` with `queryText`
- [ ] Ensure existing tests still pass.

## Step 6: Verification
- [ ] Start server using Docker fallback guide (since local Maven may not exist): `docker compose up -d --build`
- [ ] Smoke test via browser:
  - Existing cloud compare dashboard
  - Existing AI tools comparison
  - New chatbots (end-to-end)
  - NLP free-text input

