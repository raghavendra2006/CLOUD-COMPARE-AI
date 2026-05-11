# Terraform (AWS IaC) - CloudCompare AI

This folder contains Terraform to provision an AWS-native deployment for the CloudCompare AI Spring Boot app.

## Prerequisites
- Terraform installed
- AWS credentials available to Terraform runtime
  - Option B (as confirmed): set environment variables (no secrets committed):
    - `AWS_ACCESS_KEY_ID`
    - `AWS_SECRET_ACCESS_KEY`
  - Optionally `AWS_SESSION_TOKEN` if using temporary credentials

## Configure region
Edit `variables.tf` (or pass `-var` flags) to set `aws_region`.

## Deploy (example)
```bash
export AWS_ACCESS_KEY_ID=...
export AWS_SECRET_ACCESS_KEY=...

cd iac/terraform
terraform init
terraform apply \
  -var="aws_region=us-east-1" \
  -var="app_name=cloudcompare-ai"
```

## What this provisions (baseline)
- S3 bucket (for datasets/static assets placeholder)
- RDS (engine placeholder; default uses PostgreSQL) - adjust if needed
- ECS Fargate service + ALB
- API Gateway HTTP API proxying to ALB (optional wiring)

> Note: This IaC is additive and designed to not break existing local functionality.

