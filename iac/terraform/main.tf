terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
  }
}

provider "aws" {
  region     = var.aws_region
  access_key = var.aws_access_key_id
  secret_key = var.aws_secret_access_key
  token      = var.aws_session_token
}

locals {
  backend_port      = 3000
  frontend_dist_dir = "${path.module}/../../cloudcompare-frontend/dist"
  frontend_files    = fileset(local.frontend_dist_dir, "**/*")
  frontend_url      = "http://${aws_s3_bucket_website_configuration.frontend.website_endpoint}/app/"
  backend_url       = "http://${aws_instance.backend.public_ip}:${local.backend_port}"
  common_tags = {
    Project = "cloudcompare-ai"
  }
  content_types = {
    css   = "text/css"
    html  = "text/html"
    js    = "application/javascript"
    json  = "application/json"
    map   = "application/json"
    png   = "image/png"
    svg   = "image/svg+xml"
    txt   = "text/plain"
    woff  = "font/woff"
    woff2 = "font/woff2"
  }
}

data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

# 1. Security Groups
resource "aws_security_group" "backend_sg" {
  name        = "cloudcompare-backend-sg"
  description = "Security group for backend EC2"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "Backend API from browsers"
    from_port   = local.backend_port
    to_port     = local.backend_port
    protocol    = "tcp"
    cidr_blocks = var.allowed_backend_cidr_blocks
  }

  dynamic "ingress" {
    for_each = length(var.allowed_ssh_cidr_blocks) > 0 ? [1] : []

    content {
      description = "SSH access"
      from_port   = 22
      to_port     = 22
      protocol    = "tcp"
      cidr_blocks = var.allowed_ssh_cidr_blocks
    }
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(local.common_tags, {
    Name = "cloudcompare-backend-sg"
  })
}

resource "aws_security_group" "rds_sg" {
  name        = "cloudcompare-rds-sg"
  description = "Security group for RDS MySQL"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description     = "MySQL from backend EC2 only"
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.backend_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(local.common_tags, {
    Name = "cloudcompare-rds-sg"
  })
}

# 2. RDS MySQL Database
resource "aws_db_instance" "mysql" {
  identifier             = "cloudcompare-db"
  allocated_storage      = 20
  engine                 = "mysql"
  engine_version         = "8.0"
  instance_class         = var.rds_instance_class
  db_name                = var.db_name
  username               = var.db_username
  password               = var.db_password
  parameter_group_name   = "default.mysql8.0"
  skip_final_snapshot    = true
  publicly_accessible    = false
  vpc_security_group_ids = [aws_security_group.rds_sg.id]

  tags = merge(local.common_tags, {
    Name = "cloudcompare-db"
  })
}

# 3. EC2 Backend
data "aws_ami" "ubuntu" {
  most_recent = true
  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }
  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
  owners = ["099720109477"] # Canonical
}

resource "aws_instance" "backend" {
  ami                         = data.aws_ami.ubuntu.id
  instance_type               = var.backend_instance_type
  associate_public_ip_address = true
  key_name                    = var.ec2_key_name
  vpc_security_group_ids      = [aws_security_group.backend_sg.id]
  user_data_replace_on_change = true

  user_data = <<-EOF
              #!/bin/bash
              set -euo pipefail
              apt-get update
              apt-get install -y docker.io
              systemctl enable --now docker

              docker rm -f cloudcompare-ai || true
              docker pull ${var.backend_docker_image}
              docker run -d \
                --name cloudcompare-ai \
                --restart unless-stopped \
                -p ${local.backend_port}:5000 \
                -e SERVER_PORT=5000 \
                -e CORS_ALLOWED_ORIGINS=${local.frontend_url} \
                -e GROK_API_KEYS=${var.groq_api_key} \
                -e JWT_SECRET=${var.jwt_secret} \
                -e DB_HOST=${aws_db_instance.mysql.address} \
                -e DB_PORT=3306 \
                -e DB_NAME=${var.db_name} \
                -e DB_USER=${var.db_username} \
                -e DB_PASSWORD=${var.db_password} \
                -e SPRING_DATASOURCE_URL=jdbc:mysql://${aws_db_instance.mysql.address}:3306/${var.db_name}?useSSL=true\&requireSSL=false\&allowPublicKeyRetrieval=true\&serverTimezone=UTC \
                -e SPRING_DATASOURCE_USERNAME=${var.db_username} \
                -e SPRING_DATASOURCE_PASSWORD=${var.db_password} \
                -e SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver \
                -e SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQLDialect \
                -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
                ${var.backend_docker_image}
              EOF

  tags = merge(local.common_tags, {
    Name = "cloudcompare-backend"
  })
}

# 4. S3 Frontend
resource "random_id" "bucket_suffix" {
  byte_length = 4
}

resource "aws_s3_bucket" "frontend" {
  bucket = "cloudcompare-frontend-${random_id.bucket_suffix.hex}"

  tags = merge(local.common_tags, {
    Name = "cloudcompare-frontend"
  })
}

resource "aws_s3_bucket_website_configuration" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  index_document {
    suffix = "index.html"
  }

  error_document {
    key = "app/index.html"
  }
}

resource "aws_s3_bucket_public_access_block" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_policy" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "PublicReadGetObject"
        Effect    = "Allow"
        Principal = "*"
        Action    = "s3:GetObject"
        Resource  = "${aws_s3_bucket.frontend.arn}/*"
      },
    ]
  })

  depends_on = [aws_s3_bucket_public_access_block.frontend]
}

resource "aws_s3_object" "frontend_assets" {
  for_each = local.frontend_files

  bucket       = aws_s3_bucket.frontend.id
  key          = "app/${each.value}"
  source       = "${local.frontend_dist_dir}/${each.value}"
  etag         = filemd5("${local.frontend_dist_dir}/${each.value}")
  content_type = lookup(local.content_types, lower(element(split(".", each.value), length(split(".", each.value)) - 1)), "application/octet-stream")

  depends_on = [aws_s3_bucket_policy.frontend]
}

resource "aws_s3_object" "frontend_runtime_config" {
  bucket       = aws_s3_bucket.frontend.id
  key          = "app/runtime-config.js"
  content_type = "application/javascript"
  content      = "window.__CLOUDCOMPARE_CONFIG__ = { API_BASE: \"${local.backend_url}\" };\n"

  depends_on = [aws_s3_bucket_policy.frontend]
}
