package org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.project.tempo.franchisebackendsystem.domain.model.TopStockProduct;

@Schema(description = "Producto con mayor stock dentro de una sucursal.")
public record TopStockProductResponse(
        @Schema(description = "ID de la sucursal", example = "6b733aef-b0f6-46ed-a624-093be31a6387")
        String branchId,
        @Schema(description = "Nombre de la sucursal", example = "Sucursal Norte")
        String branchName,
        @Schema(description = "Producto con mayor stock de la sucursal")
        ProductResponse product
) {

    public static TopStockProductResponse fromDomain(TopStockProduct topStockProduct) {
        return new TopStockProductResponse(
                topStockProduct.branchId(),
                topStockProduct.branchName(),
                ProductResponse.fromDomain(topStockProduct.product())
        );
    }
}
