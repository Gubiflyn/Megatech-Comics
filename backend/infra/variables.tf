variable "aws_region" {
  description = "Región de AWS Academy (verifica cuál está activa en tu lab)"
  type        = string
  default     = "us-east-1"
}

variable "lab_instance_profile_name" {
  description = "Nombre del instance profile que AWS Academy ya te dio (no se crea, se referencia). Verifícalo con: aws iam list-instance-profiles"
  type        = string
  default     = "LabInstanceProfile"
}

variable "key_name" {
  description = "Nombre que tendrá el key pair en AWS (se crea a partir de tu llave pública local)"
  type        = string
  default     = "megatech-comics-key"
}

variable "public_key_path" {
  description = "Ruta local a tu llave pública"
  type        = string
  default     = "~/.ssh/megatech-comics.pub"
}

variable "private_key_path" {
  description = "Ruta local a tu llave privada"
  type        = string
  default     = "~/.ssh/megatech-comics"
}

variable "my_ip" {
  description = "Tu IP pública en formato CIDR (ej: 200.10.20.30/32)"
  type        = string
}

variable "instance_type" {
  description = "Tipo de instancia EC2"
  type        = string
  default     = "t2.medium"
}

variable "frontend_origin" {
  description = "URL donde corre el frontend React, para CORS"
  type        = string
  default     = "http://localhost:5173"
}

variable "jwt_issuer" {
  description = "Issuer de Azure AD para el JWT Authorizer del API Gateway"
  type        = string
  default     = "https://login.microsoftonline.com/f296b836-9dcb-49b9-ab6c-8e74696608f6/v2.0"
}

variable "jwt_audience" {
  description = "Audience esperado (Client ID de la API registrada en Azure AD)"
  type        = string
  default     = "79e27497-9b28-4c3c-8e33-47b3de560ce1"
}

variable "local_jwt_secret" {
  description = "Secreto HMAC compartido para tokens de clientes. DEBE ser idéntico en bff-service, carrito-service y pedidos-service."
  type        = string
  default     = "megatech-comics-local-dev-secret-cambiar-en-produccion-32chars"
  sensitive   = true
}

variable "service_env_vars" {
  description = "Variables de entorno adicionales por microservicio (datos de RDS, etc.)"
  type        = map(map(string))
  default     = {}
}
