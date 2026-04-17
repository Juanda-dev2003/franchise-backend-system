package org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud para actualizar el nombre de un producto.")
public record ProductNameRequest(
        @Schema(description = "Nombre del producto", example = "Café Premium")
        String name
) {

    public ProductNameRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
    }
}
