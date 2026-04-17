package org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.project.tempo.franchisebackendsystem.domain.model.Branch;

@Schema(description = "Sucursal asociada a una franquicia.")
public record BranchResponse(
        @Schema(description = "ID de la sucursal", example = "6b733aef-b0f6-46ed-a624-093be31a6387")
        String id,
        @Schema(description = "Nombre de la sucursal", example = "Sucursal Norte")
        String name,
        @Schema(description = "Productos de la sucursal")
        List<ProductResponse> products
) {

    public static BranchResponse fromDomain(Branch branch) {
        return new BranchResponse(
                branch.id(),
                branch.name(),
                branch.products().stream().map(ProductResponse::fromDomain).toList()
        );
    }
}
