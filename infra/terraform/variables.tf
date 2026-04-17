variable "project_name" {
  description = "Nombre base para recursos de infraestructura."
  type        = string
  default     = "franchise-backend-system"
}

variable "environment" {
  description = "Ambiente de despliegue."
  type        = string
  default     = "dev"
}

variable "aws_region" {
  description = "Region AWS donde se desplegara App Runner y ECR."
  type        = string
  default     = "us-east-1"
}

variable "container_image_tag" {
  description = "Tag de la imagen Docker que App Runner debe ejecutar."
  type        = string
  default     = "latest"
}

variable "deploy_app" {
  description = "Cuando es true crea App Runner. Primero crea ECR/Atlas, publica la imagen y luego habilita esto."
  type        = bool
  default     = false
}

variable "app_port" {
  description = "Puerto interno expuesto por la aplicacion Spring Boot."
  type        = number
  default     = 8080
}

variable "app_cpu" {
  description = "CPU para App Runner."
  type        = string
  default     = "0.25 vCPU"
}

variable "app_memory" {
  description = "Memoria para App Runner."
  type        = string
  default     = "0.5 GB"
}

variable "atlas_public_key" {
  description = "Public API key de MongoDB Atlas."
  type        = string
  sensitive   = true
}

variable "atlas_private_key" {
  description = "Private API key de MongoDB Atlas."
  type        = string
  sensitive   = true
}

variable "atlas_org_id" {
  description = "ID de la organizacion de MongoDB Atlas."
  type        = string
}

variable "atlas_project_name" {
  description = "Nombre del proyecto en MongoDB Atlas."
  type        = string
  default     = "franchise-backend-system"
}

variable "atlas_cluster_name" {
  description = "Nombre del cluster de MongoDB Atlas."
  type        = string
  default     = "franchise-cluster"
}

variable "atlas_cloud_provider" {
  description = "Proveedor cloud donde Atlas crea el cluster."
  type        = string
  default     = "AWS"
}

variable "atlas_region" {
  description = "Region de MongoDB Atlas."
  type        = string
  default     = "US_EAST_1"
}

variable "atlas_instance_size" {
  description = "Tamano de instancia Atlas. M0 es free tier cuando esta disponible."
  type        = string
  default     = "M0"
}

variable "database_name" {
  description = "Nombre de la base de datos usada por la aplicacion."
  type        = string
  default     = "franchise_backend"
}

variable "database_username" {
  description = "Usuario de base de datos que usara la aplicacion."
  type        = string
  default     = "franchise_app_user"
}

variable "atlas_access_list_cidr_blocks" {
  description = "CIDR permitidos para conectarse a Atlas. Para pruebas puede usarse 0.0.0.0/0; en produccion restringirlo."
  type        = list(string)
  default     = ["0.0.0.0/0"]
}

variable "extra_environment_variables" {
  description = "Variables adicionales para App Runner."
  type        = map(string)
  default     = {}
}
