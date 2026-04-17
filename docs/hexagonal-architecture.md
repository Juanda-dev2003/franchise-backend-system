# Hexagonal Architecture

Esta base separa el negocio de Spring, MongoDB y HTTP usando puertos y adaptadores.

## Capas

`domain`

- Contiene el modelo de negocio puro.
- No debe depender de Spring, MongoDB, WebFlux ni DTOs.
- Ubicacion actual: `src/main/java/org/project/tempo/franchisebackendsystem/domain`.

`application`

- Define los puertos de entrada y salida.
- Implementa casos de uso orquestando el dominio y los puertos.
- No debe conocer controladores, documentos Mongo ni detalles de infraestructura.
- Ubicacion actual: `src/main/java/org/project/tempo/franchisebackendsystem/application`.

`infrastructure`

- Contiene adaptadores de entrada como REST.
- Contiene adaptadores de salida como MongoDB.
- Contiene configuracion de beans para conectar puertos con implementaciones.
- Ubicacion actual: `src/main/java/org/project/tempo/franchisebackendsystem/infrastructure`.

## Flujo De Dependencias

Las dependencias deben apuntar hacia el centro:

```text
infrastructure -> application -> domain
```

El dominio no conoce la aplicacion. La aplicacion no conoce infraestructura.

## Estructura Actual

```text
domain/
  model/
  exception/

application/
  port/
    in/
    out/
  usecase/

infrastructure/
  adapter/
    in/
      web/
        dto/
    out/
      persistence/
        mongodb/
  config/
```

## Convenciones

- Agrega nuevas operaciones al puerto de entrada cuando sean acciones de usuario o del sistema.
- Agrega nuevos puertos de salida cuando el caso de uso necesite persistencia, mensajeria o servicios externos.
- Implementa cada puerto de salida dentro de `infrastructure/adapter/out`.
- Mantén los DTOs HTTP fuera de `application` y `domain`.
- Mantén las anotaciones de persistencia fuera del modelo de dominio.
