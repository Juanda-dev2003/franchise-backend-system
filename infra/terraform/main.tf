locals {
  name_prefix = "${var.project_name}-${var.environment}"
  common_tags = {
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "terraform"
  }
}

resource "random_password" "database_password" {
  length           = 24
  special          = true
  override_special = "_-"
}

resource "mongodbatlas_project" "this" {
  name   = var.atlas_project_name
  org_id = var.atlas_org_id
}

resource "mongodbatlas_advanced_cluster" "this" {
  project_id   = mongodbatlas_project.this.id
  name         = var.atlas_cluster_name
  cluster_type = "REPLICASET"

  replication_specs = [{
    region_configs = [{
      provider_name         = "TENANT"
      backing_provider_name = var.atlas_cloud_provider
      region_name           = var.atlas_region
      priority              = 7

      electable_specs = {
        instance_size = var.atlas_instance_size
      }
    }]
  }]
}

resource "mongodbatlas_database_user" "application" {
  project_id         = mongodbatlas_project.this.id
  username           = var.database_username
  password           = random_password.database_password.result
  auth_database_name = "admin"

  roles {
    role_name     = "readWrite"
    database_name = var.database_name
  }
}

resource "mongodbatlas_project_ip_access_list" "application" {
  for_each   = toset(var.atlas_access_list_cidr_blocks)
  project_id = mongodbatlas_project.this.id
  cidr_block = each.value
  comment    = "Application access ${each.value}"
}

resource "aws_ecr_repository" "application" {
  name                 = local.name_prefix
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = local.common_tags
}

resource "aws_iam_role" "apprunner_ecr_access" {
  name = "${local.name_prefix}-apprunner-ecr-access"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "build.apprunner.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })

  tags = local.common_tags
}

resource "aws_iam_role_policy_attachment" "apprunner_ecr_access" {
  role       = aws_iam_role.apprunner_ecr_access.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSAppRunnerServicePolicyForECRAccess"
}

resource "aws_apprunner_service" "application" {
  count = var.deploy_app ? 1 : 0

  service_name = local.name_prefix

  source_configuration {
    auto_deployments_enabled = true

    authentication_configuration {
      access_role_arn = aws_iam_role.apprunner_ecr_access.arn
    }

    image_repository {
      image_identifier      = "${aws_ecr_repository.application.repository_url}:${var.container_image_tag}"
      image_repository_type = "ECR"

      image_configuration {
        port = tostring(var.app_port)
        runtime_environment_variables = merge(
          {
            SERVER_PORT = tostring(var.app_port)
            MONGODB_URI = "mongodb+srv://${urlencode(mongodbatlas_database_user.application.username)}:${urlencode(random_password.database_password.result)}@${trimprefix(mongodbatlas_advanced_cluster.this.connection_strings.standard_srv, "mongodb+srv://")}/${var.database_name}?retryWrites=true&w=majority&appName=${var.atlas_cluster_name}"
          },
          var.extra_environment_variables
        )
      }
    }
  }

  instance_configuration {
    cpu    = var.app_cpu
    memory = var.app_memory
  }

  tags = local.common_tags

  depends_on = [
    aws_iam_role_policy_attachment.apprunner_ecr_access,
    mongodbatlas_project_ip_access_list.application
  ]
}
