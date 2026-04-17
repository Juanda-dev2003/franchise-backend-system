#!/usr/bin/env bash
set -euo pipefail

AWS_REGION="${AWS_REGION:-us-east-1}"
IMAGE_NAME="${IMAGE_NAME:-franchise-backend-system}"
IMAGE_TAG="${IMAGE_TAG:-latest}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

cd "${SCRIPT_DIR}"
ECR_URL="$(terraform output -raw ecr_repository_url)"
ECR_REGISTRY="$(echo "${ECR_URL}" | cut -d/ -f1)"

aws ecr get-login-password --region "${AWS_REGION}" \
  | docker login --username AWS --password-stdin "${ECR_REGISTRY}"

cd "${PROJECT_ROOT}"
docker build -t "${IMAGE_NAME}:${IMAGE_TAG}" .
docker tag "${IMAGE_NAME}:${IMAGE_TAG}" "${ECR_URL}:${IMAGE_TAG}"
docker push "${ECR_URL}:${IMAGE_TAG}"

echo "Published ${ECR_URL}:${IMAGE_TAG}"
