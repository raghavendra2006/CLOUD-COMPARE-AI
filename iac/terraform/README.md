# Terraform AWS Deployment - CloudCompare AI

This Terraform deploys CloudCompare AI into the default AWS VPC:

- React frontend hosted as an S3 static website under `/app/`
- Amazon API Gateway HTTP API as the managed HTTPS entrypoint for browser API calls
- Spring Boot backend running on EC2 with Docker
- Private Amazon RDS MySQL database for user credentials
- Security groups so only the backend can reach MySQL
- Runtime frontend config that points the browser to the API Gateway endpoint

## Prerequisites

- Terraform installed
- AWS credentials with permissions for API Gateway, EC2, RDS, S3, VPC security groups, and IAM account lookup
- Built frontend assets in `cloudcompare-frontend/dist`

Build the frontend after frontend source changes:

```bash
cd ../../cloudcompare-frontend
npm run build
```

## Secrets

Do not commit real credentials. Put secrets in environment variables or a local ignored `terraform.tfvars` file.

Environment variable example:

```bash
export TF_VAR_aws_access_key_id="..."
export TF_VAR_aws_secret_access_key="..."
export TF_VAR_db_password="use-a-strong-rds-password"
export TF_VAR_groq_api_key="..."
export TF_VAR_jwt_secret="use-a-long-random-jwt-secret"
```

Optional SSH access:

```bash
export TF_VAR_ec2_key_name="your-existing-keypair"
```

Then set `allowed_ssh_cidr_blocks` in `terraform.tfvars` if you need SSH.

## Deploy

```bash
cd iac/terraform
terraform init
terraform plan
terraform apply
```

## Outputs

Terraform prints:

- `frontend_url`
- `api_gateway_url`
- `backend_public_ip`
- `backend_url`
- `rds_endpoint`
- `frontend_bucket_name`

The frontend URL is the public S3 website URL. The frontend calls `api_gateway_url`, which provides an HTTPS API endpoint with CORS and throttling before proxying requests to the EC2 backend. The backend connects to RDS using private networking inside the default VPC.
