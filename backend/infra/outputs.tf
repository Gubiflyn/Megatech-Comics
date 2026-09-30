output "elastic_ip" {
  description = "IP publica fija de la instancia"
  value       = aws_eip.app_eip.public_ip
}

output "api_gateway_url" {
  description = "URL publica del API Gateway"
  value       = aws_apigatewayv2_api.http_api.api_endpoint
}

output "ssh_command" {
  description = "Comando para conectarte por SSH"
  value       = "ssh -i ${var.private_key_path} ubuntu@${aws_eip.app_eip.public_ip}"
}
