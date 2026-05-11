variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "aws_access_key_id" {
  description = "AWS access key ID. Prefer TF_VAR_aws_access_key_id or AWS_ACCESS_KEY_ID instead of committing this."
  type        = string
  sensitive   = true
  default     = null
}

variable "aws_secret_access_key" {
  description = "AWS secret access key. Prefer TF_VAR_aws_secret_access_key or AWS_SECRET_ACCESS_KEY instead of committing this."
  type        = string
  sensitive   = true
  default     = null
}

variable "aws_session_token" {
  description = "Optional AWS session token for temporary credentials."
  type        = string
  sensitive   = true
  default     = null
}

variable "db_password" {
  description = "RDS MySQL password"
  type        = string
  sensitive   = true
}

variable "db_username" {
  description = "RDS MySQL username"
  default     = "root"
  type        = string
}

variable "db_name" {
  description = "RDS MySQL database name"
  default     = "cloudcompare"
  type        = string
}

variable "groq_api_key" {
  description = "Groq API Key for AI Engine"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "Secret used to sign JWT tokens."
  type        = string
  sensitive   = true
}

variable "backend_docker_image" {
  description = "Docker image used for the Spring Boot backend."
  type        = string
  default     = "raghavendra76/cloudcompare-ai:latest"
}

variable "backend_instance_type" {
  description = "EC2 instance type for the backend."
  type        = string
  default     = "t2.micro"
}

variable "rds_instance_class" {
  description = "RDS instance class for MySQL."
  type        = string
  default     = "db.t3.micro"
}

variable "ec2_key_name" {
  description = "Optional existing EC2 key pair name for SSH access."
  type        = string
  default     = null
}

variable "allowed_ssh_cidr_blocks" {
  description = "CIDR blocks allowed to SSH into the backend."
  type        = list(string)
  default     = []
}

variable "allowed_backend_cidr_blocks" {
  description = "CIDR blocks allowed to reach the public backend API."
  type        = list(string)
  default     = ["0.0.0.0/0"]
}
