output "ecr_repository_urls" {
  description = "URLs of the ECR repositories"
  value = {
    frontend     = aws_ecr_repository.ecommerce_frontend.repository_url
    user_service = aws_ecr_repository.ecommerce_user_service.repository_url
    product_service = aws_ecr_repository.ecommerce_product_service.repository_url
    order_service   = aws_ecr_repository.ecommerce_order_service.repository_url
    payment_service = aws_ecr_repository.ecommerce_payment_service.repository_url
    inventory_service = aws_ecr_repository.ecommerce_inventory_service.repository_url
    notification_service = aws_ecr_repository.ecommerce_notification_service.repository_url
    
  }
}

output "ecr_repository_names" {
  description = "Names of the ECR repositories"
  value = [
    aws_ecr_repository.ecommerce_frontend.name,
    aws_ecr_repository.ecommerce_user_service.name,
    aws_ecr_repository.ecommerce_product_service.name,
    aws_ecr_repository.ecommerce_order_service.name,
    aws_ecr_repository.ecommerce_payment_service.name,
    aws_ecr_repository.ecommerce_inventory_service.name,
    aws_ecr_repository.ecommerce_notification_service.name,
  ]
}