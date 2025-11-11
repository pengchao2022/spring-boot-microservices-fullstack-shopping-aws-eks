output "api_gateway_id" {
  description = "API Gateway ID"
  value       = aws_api_gateway_rest_api.ecommerce_api.id
}

output "api_gateway_root_resource_id" {
  description = "API Gateway root resource ID"
  value       = aws_api_gateway_rest_api.ecommerce_api.root_resource_id
}

output "api_gateway_execution_arn" {
  description = "API Gateway execution ARN"
  value       = aws_api_gateway_rest_api.ecommerce_api.execution_arn
}

output "api_gateway_invoke_url" {
  description = "API Gateway invoke URL"
  value       = "${aws_api_gateway_deployment.deployment.invoke_url}/"  # 修复：改为 deployment
}

output "api_custom_domain_url" {
  description = "API Gateway custom domain URL"
  value       = "https://${aws_api_gateway_domain_name.api_domain.domain_name}"
}

output "api_gateway_domain_name" {
  description = "API Gateway custom domain name"
  value       = aws_api_gateway_domain_name.api_domain.domain_name
}

output "api_gateway_regional_domain_name" {
  description = "API Gateway regional domain name"
  value       = aws_api_gateway_domain_name.api_domain.regional_domain_name
}

output "api_gateway_regional_zone_id" {
  description = "API Gateway regional zone ID"
  value       = aws_api_gateway_domain_name.api_domain.regional_zone_id
}

output "vpc_link_id" {
  description = "API Gateway VPC Link ID"
  value       = aws_api_gateway_vpc_link.nlb_vpc_link.id
}

output "nlb_zone_id" {
  description = "NLB zone ID"
  value       = data.aws_lb.microservices_nlb.zone_id
}


# 有用的部署信息
output "deployment_information" {
  description = "Deployment information for API Gateway"
  value = {
    api_url           = "${aws_api_gateway_deployment.deployment.invoke_url}/"  # 修复：改为 deployment
    custom_domain_url = "https://${aws_api_gateway_domain_name.api_domain.domain_name}"
    nlb_endpoint      = data.aws_lb.microservices_nlb.dns_name
    stage_name        = var.api_stage_name
    region            = var.aws_region
  }
}

# DNS 记录信息（用于 Route53 配置）
output "dns_configuration" {
  description = "DNS configuration information"
  value = {
    api_regional_domain  = aws_api_gateway_domain_name.api_domain.regional_domain_name
    api_regional_zone_id = aws_api_gateway_domain_name.api_domain.regional_zone_id
    nlb_dns_name         = data.aws_lb.microservices_nlb.dns_name
    nlb_zone_id          = data.aws_lb.microservices_nlb.zone_id
  }
}