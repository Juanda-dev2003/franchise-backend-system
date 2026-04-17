package org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud para crear o actualizar una franquicia.")
public record FranchiseRequest(
        @Schema(description = "Nombre de la franquicia", example = "Franquicia Centro")
        String name
) {

    public FranchiseRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Franchise name is required");
        }
    }
}
