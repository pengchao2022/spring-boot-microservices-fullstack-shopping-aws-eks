terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  backend "s3" {
    bucket         = "terraformstatefile090909"
    key            = "ecr_state.tfstate"
    region         = "us-east-1"
    # dynamodb_table = "terraform-locks"
    encrypt        = true
  }
}

provider "aws" {
  region = "us-east-1"
}

# ECR Repository for Frontend
resource "aws_ecr_repository" "ecommerce_frontend" {
  name                 = "ecommerce-frontend"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  encryption_configuration {
    encryption_type = "AES256"
  }

  tags = {
    Project     = "ecommerce"
    Environment = "production"
    Component   = "frontend"
  }
}

# ECR Repository for User Service
resource "aws_ecr_repository" "ecommerce_user_service" {
  name                 = "ecommerce-user-service"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  encryption_configuration {
    encryption_type = "AES256"
  }

  tags = {
    Project     = "ecommerce"
    Environment = "production"
    Component   = "backend"
    Service     = "user-service"
  }
}

# ECR Repository for Product Service
resource "aws_ecr_repository" "ecommerce_product_service" {
  name                 = "ecommerce-product-service"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  encryption_configuration {
    encryption_type = "AES256"
  }

  tags = {
    Project     = "ecommerce"
    Environment = "production"
    Component   = "backend"
    Service     = "product-service"
  }
}

# ECR Repository for Order Service
resource "aws_ecr_repository" "ecommerce_order_service" {
  name                 = "ecommerce-order-service"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  encryption_configuration {
    encryption_type = "AES256"
  }

  tags = {
    Project     = "ecommerce"
    Environment = "production"
    Component   = "backend"
    Service     = "order-service"
  }
}

# ECR Repository for Payment Service
resource "aws_ecr_repository" "ecommerce_payment_service" {
  name                 = "ecommerce-payment-service"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  encryption_configuration {
    encryption_type = "AES256"
  }

  tags = {
    Project     = "ecommerce"
    Environment = "production"
    Component   = "backend"
    Service     = "payment-service"
  }
}

# ECR Repository for Inventory Service
resource "aws_ecr_repository" "ecommerce_inventory_service" {
  name                 = "ecommerce-inventory-service"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  encryption_configuration {
    encryption_type = "AES256"
  }

  tags = {
    Project     = "ecommerce"
    Environment = "production"
    Component   = "backend"
    Service     = "inventory-service"
  }
}

# ECR Repository for Notification Service
resource "aws_ecr_repository" "ecommerce_notification_service" {
  name                 = "ecommerce-notification-service"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  encryption_configuration {
    encryption_type = "AES256"
  }

  tags = {
    Project     = "ecommerce"
    Environment = "production"
    Component   = "backend"
    Service     = "notification-service"
  }
}

# ECR Lifecycle Policies
resource "aws_ecr_lifecycle_policy" "ecr_lifecycle_policy" {
  for_each = {
    frontend     = aws_ecr_repository.ecommerce_frontend.name
    user         = aws_ecr_repository.ecommerce_user_service.name
    product      = aws_ecr_repository.ecommerce_product_service.name
    order        = aws_ecr_repository.ecommerce_order_service.name
    payment      = aws_ecr_repository.ecommerce_payment_service.name
    inventory    = aws_ecr_repository.ecommerce_inventory_service.name
    notification = aws_ecr_repository.ecommerce_notification_service.name
    
  }

  repository = each.value
  policy     = <<EOF
{
  "rules": [
    {
      "rulePriority": 1,
      "description": "Remove untagged images older than 7 days",
      "selection": {
        "tagStatus": "untagged",
        "countType": "sinceImagePushed",
        "countUnit": "days",
        "countNumber": 7
      },
      "action": {
        "type": "expire"
      }
    },
    {
      "rulePriority": 2,
      "description": "Keep last 30 images",
      "selection": {
        "tagStatus": "any",
        "countType": "imageCountMoreThan",
        "countNumber": 30
      },
      "action": {
        "type": "expire"
      }
    }
  ]
}
EOF
}