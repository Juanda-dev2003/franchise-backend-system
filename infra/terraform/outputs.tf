output "ecr_repository_url" {
  description = "URL del repositorio ECR donde se debe publicar la imagen Docker."
  value       = aws_ecr_repository.application.repository_url
}

output "apprunner_service_url" {
  description = "URL publica de la API desplegada en AWS App Runner."
  value       = var.deploy_app ? aws_apprunner_service.application[0].service_url : null
}

output "atlas_project_id" {
  description = "ID del proyecto MongoDB Atlas."
  value       = mongodbatlas_project.this.id
}

output "atlas_cluster_name" {
  description = "Nombre del cluster MongoDB Atlas."
  value       = mongodbatlas_advanced_cluster.this.name
}

output "database_username" {
  description = "Usuario de base de datos usado por la aplicacion."
  value       = mongodbatlas_database_user.application.username
}
