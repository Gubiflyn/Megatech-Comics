resource "aws_apigatewayv2_api" "http_api" {
  name          = "megatech-comics-api"
  protocol_type = "HTTP"

  cors_configuration {
    allow_origins = [var.frontend_origin]
    allow_methods = ["GET", "POST", "PUT", "DELETE", "OPTIONS"]
    allow_headers = ["Authorization", "Content-Type"]
    max_age       = 300
  }
}

resource "aws_apigatewayv2_authorizer" "jwt_auth" {
  api_id           = aws_apigatewayv2_api.http_api.id
  authorizer_type  = "JWT"
  identity_sources = ["$request.header.Authorization"]
  name             = "azure-ad-jwt-authorizer"

  jwt_configuration {
    audience = [var.jwt_audience]
    issuer   = var.jwt_issuer
  }
}

resource "aws_apigatewayv2_integration" "bff_integration" {
  api_id                 = aws_apigatewayv2_api.http_api.id
  integration_type       = "HTTP_PROXY"
  integration_method     = "ANY"
  integration_uri        = "http://${aws_eip.app_eip.public_ip}:8080/api/{proxy}"
  payload_format_version = "1.0"
}

resource "aws_apigatewayv2_integration" "catalogo_listado_integration" {
  api_id                 = aws_apigatewayv2_api.http_api.id
  integration_type       = "HTTP_PROXY"
  integration_method     = "GET"
  integration_uri        = "http://${aws_eip.app_eip.public_ip}:8080/api/catalogo"
  payload_format_version = "1.0"
}

resource "aws_apigatewayv2_integration" "catalogo_detalle_integration" {
  api_id                 = aws_apigatewayv2_api.http_api.id
  integration_type       = "HTTP_PROXY"
  integration_method     = "ANY"
  integration_uri        = "http://${aws_eip.app_eip.public_ip}:8080/api/catalogo/{proxy}"
  payload_format_version = "1.0"
}

resource "aws_apigatewayv2_integration" "auth_clientes_integration" {
  api_id                 = aws_apigatewayv2_api.http_api.id
  integration_type       = "HTTP_PROXY"
  integration_method     = "ANY"
  integration_uri        = "http://${aws_eip.app_eip.public_ip}:8080/api/auth/clientes/{proxy}"
  payload_format_version = "1.0"
}

resource "aws_apigatewayv2_route" "proxy_route" {
  api_id             = aws_apigatewayv2_api.http_api.id
  route_key          = "ANY /api/{proxy+}"
  target             = "integrations/${aws_apigatewayv2_integration.bff_integration.id}"
  authorization_type = "JWT"
  authorizer_id      = aws_apigatewayv2_authorizer.jwt_auth.id
}

resource "aws_apigatewayv2_route" "options_proxy" {
  api_id             = aws_apigatewayv2_api.http_api.id
  route_key          = "OPTIONS /api/{proxy+}"
  target             = "integrations/${aws_apigatewayv2_integration.bff_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "catalogo_publico" {
  api_id             = aws_apigatewayv2_api.http_api.id
  route_key          = "GET /api/catalogo"
  target             = "integrations/${aws_apigatewayv2_integration.catalogo_listado_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "catalogo_publico_detalle" {
  api_id             = aws_apigatewayv2_api.http_api.id
  route_key          = "GET /api/catalogo/{proxy+}"
  target             = "integrations/${aws_apigatewayv2_integration.catalogo_detalle_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "auth_clientes_publico" {
  api_id             = aws_apigatewayv2_api.http_api.id
  route_key          = "POST /api/auth/clientes/{proxy+}"
  target             = "integrations/${aws_apigatewayv2_integration.auth_clientes_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.http_api.id
  name        = "$default"
  auto_deploy = true
}
