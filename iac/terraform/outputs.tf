output "frontend_url" {
  description = "URL for the S3 static website"
  value       = local.frontend_url
}

output "backend_public_ip" {
  description = "Public IP of the EC2 Backend"
  value       = aws_instance.backend.public_ip
}

output "backend_url" {
  description = "Public URL of the backend API"
  value       = local.backend_url
}

output "rds_endpoint" {
  description = "RDS MySQL Endpoint"
  value       = aws_db_instance.mysql.endpoint
}

output "frontend_bucket_name" {
  description = "Name of the S3 bucket for frontend hosting"
  value       = aws_s3_bucket.frontend.bucket
}
