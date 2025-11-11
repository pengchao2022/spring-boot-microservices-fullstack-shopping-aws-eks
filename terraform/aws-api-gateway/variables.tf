variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "production"
}

variable "project_name" {
  description = "Project name"
  type        = string
  default     = "ecommerce"
}

variable "api_domain_name" {
  description = "API Gateway custom domain name"
  type        = string
  default     = "api.awsmpc.asia"
}

variable "wildcard_cert_domain" {
  description = "Wildcard certificate domain"
  type        = string
  default     = "*.awsmpc.asia"
}

variable "vpc_link_name" {
  description = "API Gateway VPC Link name"
  type        = string
  default     = "ecommerce-nlb-vpc-link"
}

variable "api_gateway_name" {
  description = "API Gateway name"
  type        = string
  default     = "ecommerce-api-gateway"
}

variable "api_stage_name" {
  description = "API Gateway stage name"
  type        = string
  default     = "prod"
}