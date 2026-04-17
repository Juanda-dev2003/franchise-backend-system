package org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.project.tempo.franchisebackendsystem.domain.model.Product;

@Schema(description = "Producto de una sucursal.")
public record ProductResponse(
        @Schema(description = "ID del producto", example = "975f69e0-1979-4ce7-9708-ecb72f2a92ea")
        String id,
        @Schema(description = "Nombre del producto", example = "Café")
        String name,
        @Schema(description = "Cantidad disponible en stock", example = "25", minimum = "0")
        int stock
) {

    public static ProductResponse fromDomain(Product product) {
        return new ProductResponse(product.id(), product.name(), product.stock());
    }
}
