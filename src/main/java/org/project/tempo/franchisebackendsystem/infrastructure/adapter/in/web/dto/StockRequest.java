package org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud para actualizar stock.")
public record StockRequest(
        @Schema(description = "Nueva cantidad de stock", example = "40", minimum = "0")
        int stock
) {

    public StockRequest {
        if (stock < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative");
        }
    }
}
