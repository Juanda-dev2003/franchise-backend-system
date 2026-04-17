package org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.project.tempo.franchisebackendsystem.domain.model.Franchise;

@Schema(description = "Franquicia con sus sucursales y productos.")
public record FranchiseResponse(
        @Schema(description = "ID de la franquicia", example = "f4f7a6f5-865d-4c6a-8b4a-23dbd21c8af1")
        String id,
        @Schema(description = "Nombre de la franquicia", example = "Franquicia Centro")
        String name,
        @Schema(description = "Sucursales asociadas a la franquicia")
        List<BranchResponse> branches
) {

    public static FranchiseResponse fromDomain(Franchise franchise) {
        return new FranchiseResponse(
                franchise.id(),
                franchise.name(),
                franchise.branches().stream().map(BranchResponse::fromDomain).toList()
        );
    }
}
