# 查询由 K8s 创建的 NLB
data "aws_lb" "microservices_nlb" {
  name = "k8s-ecommerc-microser-9e1de5bd30"
}

# 创建 REST API Gateway
resource "aws_api_gateway_rest_api" "ecommerce_api" {
  name        = var.api_gateway_name
  description = "API Gateway for Ecommerce Microservices"
  
  endpoint_configuration {
    types = ["REGIONAL"]
  }

  tags = {
    Environment = var.environment
    Project     = var.project_name
  }
}

# 创建代理资源，捕获所有路径
resource "aws_api_gateway_resource" "proxy" {
  rest_api_id = aws_api_gateway_rest_api.ecommerce_api.id
  parent_id   = aws_api_gateway_rest_api.ecommerce_api.root_resource_id
  path_part   = "{proxy+}"
}

# 为代理资源创建 ANY 方法
resource "aws_api_gateway_method" "proxy" {
  rest_api_id   = aws_api_gateway_rest_api.ecommerce_api.id
  resource_id   = aws_api_gateway_resource.proxy.id
  http_method   = "ANY"
  authorization = "NONE"
  
  request_parameters = {
    "method.request.path.proxy" = true
  }
}

# 为根路径创建方法
resource "aws_api_gateway_method" "root" {
  rest_api_id   = aws_api_gateway_rest_api.ecommerce_api.id
  resource_id   = aws_api_gateway_rest_api.ecommerce_api.root_resource_id
  http_method   = "ANY"
  authorization = "NONE"
}

# 创建 VPC Link 
resource "aws_api_gateway_vpc_link" "nlb_vpc_link" {
  name        = var.vpc_link_name
  target_arns = [data.aws_lb.microservices_nlb.arn]
  description = "VPC Link for internal Ecommerce NLB"

  tags = {
    Environment = var.environment
    Project     = var.project_name
  }
}

# 修改：创建与 K8s NLB 的集成 - 代理路径（添加 /api 前缀）
resource "aws_api_gateway_integration" "proxy_integration" {
  rest_api_id = aws_api_gateway_rest_api.ecommerce_api.id
  resource_id = aws_api_gateway_resource.proxy.id
  http_method = aws_api_gateway_method.proxy.http_method
  
  integration_http_method = "ANY"
  type                    = "HTTP_PROXY"
  uri                     = "https://${data.aws_lb.microservices_nlb.dns_name}:443/api/{proxy}"  # 修改：添加 /api 前缀
  connection_type         = "VPC_LINK" 
  connection_id           = aws_api_gateway_vpc_link.nlb_vpc_link.id

  request_parameters = {
    "integration.request.path.proxy" = "method.request.path.proxy"
  }
}

# 修改：创建与 K8s NLB 的集成 - 根路径（添加 /api 前缀）
resource "aws_api_gateway_integration" "root_integration" {
  rest_api_id = aws_api_gateway_rest_api.ecommerce_api.id
  resource_id = aws_api_gateway_rest_api.ecommerce_api.root_resource_id
  http_method = aws_api_gateway_method.root.http_method
  
  integration_http_method = "ANY"
  type                    = "HTTP_PROXY"
  uri                     = "https://${data.aws_lb.microservices_nlb.dns_name}:443/api/"  # 修改：添加 /api 前缀
  connection_type         = "VPC_LINK"
  connection_id           = aws_api_gateway_vpc_link.nlb_vpc_link.id
}

# 为代理路径添加方法响应
resource "aws_api_gateway_method_response" "proxy_200" {
  rest_api_id = aws_api_gateway_rest_api.ecommerce_api.id
  resource_id = aws_api_gateway_resource.proxy.id
  http_method = aws_api_gateway_method.proxy.http_method
  status_code = "200"
}

resource "aws_api_gateway_method_response" "proxy_500" {
  rest_api_id = aws_api_gateway_rest_api.ecommerce_api.id
  resource_id = aws_api_gateway_resource.proxy.id
  http_method = aws_api_gateway_method.proxy.http_method
  status_code = "500"
}

# 为代理路径添加集成响应
resource "aws_api_gateway_integration_response" "proxy_200" {
  rest_api_id = aws_api_gateway_rest_api.ecommerce_api.id
  resource_id = aws_api_gateway_resource.proxy.id
  http_method = aws_api_gateway_method.proxy.http_method
  status_code = aws_api_gateway_method_response.proxy_200.status_code

  depends_on = [aws_api_gateway_integration.proxy_integration]
}

resource "aws_api_gateway_integration_response" "proxy_500" {
  rest_api_id = aws_api_gateway_rest_api.ecommerce_api.id
  resource_id = aws_api_gateway_resource.proxy.id
  http_method = aws_api_gateway_method.proxy.http_method
  status_code = aws_api_gateway_method_response.proxy_500.status_code
  selection_pattern = "5\\d{2}"

  depends_on = [aws_api_gateway_integration.proxy_integration]
}

# 为根路径添加方法响应
resource "aws_api_gateway_method_response" "root_200" {
  rest_api_id = aws_api_gateway_rest_api.ecommerce_api.id
  resource_id = aws_api_gateway_rest_api.ecommerce_api.root_resource_id
  http_method = aws_api_gateway_method.root.http_method
  status_code = "200"
}

resource "aws_api_gateway_method_response" "root_500" {
  rest_api_id = aws_api_gateway_rest_api.ecommerce_api.id
  resource_id = aws_api_gateway_rest_api.ecommerce_api.root_resource_id
  http_method = aws_api_gateway_method.root.http_method
  status_code = "500"
}

# 为根路径添加集成响应
resource "aws_api_gateway_integration_response" "root_200" {
  rest_api_id = aws_api_gateway_rest_api.ecommerce_api.id
  resource_id = aws_api_gateway_rest_api.ecommerce_api.root_resource_id
  http_method = aws_api_gateway_method.root.http_method
  status_code = aws_api_gateway_method_response.root_200.status_code

  depends_on = [aws_api_gateway_integration.root_integration]
}

resource "aws_api_gateway_integration_response" "root_500" {
  rest_api_id = aws_api_gateway_rest_api.ecommerce_api.id
  resource_id = aws_api_gateway_rest_api.ecommerce_api.root_resource_id
  http_method = aws_api_gateway_method.root.http_method
  status_code = aws_api_gateway_method_response.root_500.status_code
  selection_pattern = "5\\d{2}"

  depends_on = [aws_api_gateway_integration.root_integration]
}

# 创建部署
resource "aws_api_gateway_deployment" "deployment" {
  depends_on = [
    aws_api_gateway_integration.proxy_integration,
    aws_api_gateway_integration.root_integration,
    aws_api_gateway_integration_response.proxy_200,
    aws_api_gateway_integration_response.proxy_500,
    aws_api_gateway_integration_response.root_200,
    aws_api_gateway_integration_response.root_500,
    aws_api_gateway_vpc_link.nlb_vpc_link
  ]
  
  rest_api_id = aws_api_gateway_rest_api.ecommerce_api.id

  triggers = {
    redeployment = sha1(jsonencode([
      aws_api_gateway_resource.proxy.id,
      aws_api_gateway_method.proxy.id,
      aws_api_gateway_method.root.id,
      aws_api_gateway_integration.proxy_integration.id,
      aws_api_gateway_integration.root_integration.id,
      aws_api_gateway_vpc_link.nlb_vpc_link.id
    ]))
  }

  lifecycle {
    create_before_destroy = true
  }
}

# 创建阶段
resource "aws_api_gateway_stage" "prod" {
  deployment_id = aws_api_gateway_deployment.deployment.id
  rest_api_id   = aws_api_gateway_rest_api.ecommerce_api.id
  stage_name    = var.api_stage_name
}

# 自定义域名
resource "aws_api_gateway_domain_name" "api_domain" {
  domain_name              = var.api_domain_name
  regional_certificate_arn = "arn:aws:acm:us-east-1:319998871902:certificate/025be155-2b14-4804-a39f-cc02f7d051d0" 

  endpoint_configuration {
    types = ["REGIONAL"]
  }

  tags = {
    Environment = var.environment
    Project     = var.project_name
  }
}

# API 映射到自定义域名
resource "aws_api_gateway_base_path_mapping" "api_mapping" {
  api_id      = aws_api_gateway_rest_api.ecommerce_api.id
  domain_name = aws_api_gateway_domain_name.api_domain.domain_name
  stage_name  = aws_api_gateway_stage.prod.stage_name
}