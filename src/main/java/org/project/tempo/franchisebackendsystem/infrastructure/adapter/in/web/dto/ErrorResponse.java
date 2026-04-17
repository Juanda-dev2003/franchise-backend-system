package org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de error.")
public record ErrorResponse(
        @Schema(description = "Mensaje de error", example = "Franchise not found: franchise-id")
        String message
) {
}
