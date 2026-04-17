package org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud para crear un producto.")
public record ProductRequest(
        @Schema(description = "Nombre del producto", example = "Café")
        String name,
        @Schema(description = "Cantidad disponible en stock", example = "25", minimum = "0")
        int stock
) {

    public ProductRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative");
        }
    }
}
