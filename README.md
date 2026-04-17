# Franchise Backend System

API reactiva para administrar franquicias, sucursales y productos.

## Stack

- Java 17
- Spring Boot 4
- Spring WebFlux
- Spring Data MongoDB Reactive
- Gradle
- MongoDB
- Docker

## Arquitectura

El proyecto usa arquitectura hexagonal, compatible con una separación limpia entre controller, service y repository.

```text
src/main/java/org/project/tempo/franchisebackendsystem
├── domain
│   ├── exception
│   └── model
├── application
│   ├── port
│   │   ├── in
│   │   └── out
│   └── usecase
└── infrastructure
    ├── adapter
    │   ├── in
    │   │   └── web
    │   │       └── dto
    │   └── out
    │       └── persistence
    │           └── mongodb
    └── config
```

## Partes principales

- `domain/model`: entidades de negocio puras: franquicia, sucursal y producto.
- `application/port/in`: contrato de casos de uso que consume el controlador.
- `application/port/out`: contrato de persistencia que implementa la infraestructura.
- `application/usecase`: servicio con la lógica de negocio.
- `infrastructure/adapter/in/web`: controller REST, DTOs y manejo de errores.
- `infrastructure/adapter/out/persistence/mongodb`: documentos, mapper y repositorio Mongo reactivo.
- `infrastructure/config`: configuración para conectar puertos con implementaciones.

## Base de datos

La implementación incluida usa MongoDB reactivo. La URI se configura con la variable `MONGODB_URI`.

Valor por defecto:

```text
mongodb://localhost:27017/franchise_backend
```

## Ejecutar localmente

Necesitas Java 17 y MongoDB corriendo en `localhost:27017`.

```bash
./gradlew bootRun
```

Con una URI personalizada:

```bash
MONGODB_URI=mongodb://localhost:27017/franchise_backend ./gradlew bootRun
```

## Ejecutar con Docker

```bash
docker compose up --build
```

La API queda disponible en:

```text
http://localhost:8080
```

## Despliegue con Terraform

La infraestructura como codigo esta en:

```text
infra/terraform
```

Incluye MongoDB Atlas, AWS ECR y AWS App Runner. La guia de ejecucion esta en `infra/terraform/README.md`.

## Ejecutar tests

```bash
./gradlew test
```

## Documentacion Swagger

Cuando la aplicacion este corriendo, la documentacion interactiva esta disponible en:

```text
http://localhost:8080/swagger-ui.html
```

El contrato OpenAPI en JSON esta disponible en:

```text
http://localhost:8080/v3/api-docs
```

## Endpoints

### 1. Crear franquicia

```bash
curl -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name":"Franquicia Centro"}'
```

Respuesta:

```json
{
  "id": "5a9d4f2e-4b0d-4f3b-8d3e-4b9b1b0f5c8a",
  "name": "Franquicia Centro",
  "branches": []
}
```

### 2. Agregar sucursal a una franquicia

```bash
curl -X POST http://localhost:8080/api/franchises/{franchiseId}/branches \
  -H "Content-Type: application/json" \
  -d '{"name":"Sucursal Norte"}'
```

### 3. Agregar producto a una sucursal

```bash
curl -X POST http://localhost:8080/api/franchises/{franchiseId}/branches/{branchId}/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Café","stock":25}'
```

### 4. Eliminar producto de una sucursal

```bash
curl -X DELETE http://localhost:8080/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}
```

Respuesta:

```json
{
  "id": "franchise-id",
  "name": "Franquicia Centro",
  "branches": [
    {
      "id": "branch-id",
      "name": "Sucursal Norte",
      "products": []
    }
  ]
}
```

### 5. Modificar stock de un producto

```bash
curl -X PATCH http://localhost:8080/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock \
  -H "Content-Type: application/json" \
  -d '{"stock":40}'
```

### 6. Obtener producto con mayor stock por sucursal

```bash
curl http://localhost:8080/api/franchises/{franchiseId}/top-stock-products
```

Respuesta:

```json
[
  {
    "branchId": "branch-id-1",
    "branchName": "Sucursal Norte",
    "product": {
      "id": "product-id-1",
      "name": "Café",
      "stock": 40
    }
  },
  {
    "branchId": "branch-id-2",
    "branchName": "Sucursal Sur",
    "product": {
      "id": "product-id-2",
      "name": "Té",
      "stock": 18
    }
  }
]
```

Las sucursales sin productos no aparecen en la respuesta.

## Endpoints extra

### Actualizar nombre de franquicia

```bash
curl -X PATCH http://localhost:8080/api/franchises/{franchiseId} \
  -H "Content-Type: application/json" \
  -d '{"name":"Nuevo nombre"}'
```

### Actualizar nombre de sucursal

```bash
curl -X PATCH http://localhost:8080/api/franchises/{franchiseId}/branches/{branchId} \
  -H "Content-Type: application/json" \
  -d '{"name":"Sucursal Actualizada"}'
```

### Actualizar nombre de producto

```bash
curl -X PATCH http://localhost:8080/api/franchises/{franchiseId}/branches/{branchId}/products/{productId} \
  -H "Content-Type: application/json" \
  -d '{"name":"Producto Actualizado"}'
```

### Listar franquicias

```bash
curl http://localhost:8080/api/franchises
```

### Consultar franquicia por ID

```bash
curl http://localhost:8080/api/franchises/{franchiseId}
```

### Eliminar franquicia

```bash
curl -X DELETE http://localhost:8080/api/franchises/{franchiseId}
```

## Validaciones y errores

Campos obligatorios:

- `name` no puede ser nulo ni vacío.
- `stock` no puede ser negativo.

Ejemplo de error:

```json
{
  "message": "Product stock cannot be negative"
}
```

Si no existe una franquicia, sucursal o producto, la API responde `404`.

## Modelo de datos MongoDB

Colección:

```text
franchises
```

Documento:

```json
{
  "_id": "franchise-id",
  "name": "Franquicia Centro",
  "branches": [
    {
      "id": "branch-id",
      "name": "Sucursal Norte",
      "products": [
        {
          "id": "product-id",
          "name": "Café",
          "stock": 40
        }
      ]
    }
  ]
}
```

## Indices MongoDB

La coleccion `franchises` crea indices al iniciar la aplicacion para acelerar consultas por campos frecuentes:

```text
_id                         -> creado automaticamente por MongoDB
name                        -> idx_franchise_name
branches.id                 -> idx_branch_id
branches.name               -> idx_branch_name
branches.products.id        -> idx_product_id
branches.products.name      -> idx_product_name
```

Los indices se configuran en `MongoIndexConfiguration`.
