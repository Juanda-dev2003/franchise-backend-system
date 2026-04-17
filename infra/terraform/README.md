# Terraform

Esta carpeta gestiona dos partes de la infraestructura:

- MongoDB Atlas: proyecto, cluster, usuario de base de datos y access list.
- AWS: repositorio ECR y despliegue de la API en App Runner.

## Requisitos

- Terraform >= 1.6
- AWS CLI autenticado
- Cuenta de MongoDB Atlas
- API keys de MongoDB Atlas
- Docker

## Variables sensibles

No guardes secretos en archivos versionados. Exporta las credenciales:

```bash
export TF_VAR_atlas_public_key="atlas-public-key"
export TF_VAR_atlas_private_key="atlas-private-key"
```

AWS debe estar autenticado por el mecanismo normal de Terraform/AWS CLI:

```bash
aws configure
```

## Configuracion

Copia el archivo de ejemplo:

```bash
cp terraform.tfvars.example terraform.tfvars
```

Edita:

```text
atlas_org_id
atlas_project_name
atlas_cluster_name
aws_region
atlas_access_list_cidr_blocks
```

Para pruebas se permite `0.0.0.0/0`, pero en produccion debes restringirlo.

## Crear infraestructura

Primero crea Atlas y ECR sin App Runner. Esto evita que App Runner falle porque la imagen aun no existe:

```bash
terraform init
terraform plan
terraform apply
```

Terraform imprimira el repositorio ECR y la URL publica de App Runner.

## Publicar imagen Docker

Primero crea la infraestructura para obtener el ECR:

```bash
terraform output ecr_repository_url
```

Autentica Docker contra ECR:

```bash
aws ecr get-login-password --region us-east-1 \
  | docker login --username AWS --password-stdin "$(terraform output -raw ecr_repository_url | cut -d/ -f1)"
```

Construye y publica la imagen:

```bash
chmod +x push-image.sh
./push-image.sh
```

App Runner tiene `auto_deployments_enabled = true`, por lo que tomara la nueva imagen.

## Crear App Runner

Despues de publicar la imagen, habilita el despliegue:

```hcl
deploy_app = true
```

Luego ejecuta:

```bash
terraform apply
```

## Consultar la API

```bash
terraform output apprunner_service_url
```

Swagger:

```text
https://<apprunner-url>/swagger-ui.html
```

OpenAPI:

```text
https://<apprunner-url>/v3/api-docs
```

## Destruir infraestructura

```bash
terraform destroy
```

Esto elimina App Runner, ECR y recursos de Atlas creados por este estado.
