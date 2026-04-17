package org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud para crear o actualizar una sucursal.")
public record BranchRequest(
        @Schema(description = "Nombre de la sucursal", example = "Sucursal Norte")
        String name
) {

    public BranchRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Branch name is required");
        }
    }
}
