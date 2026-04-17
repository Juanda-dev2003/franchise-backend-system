package org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.project.tempo.franchisebackendsystem.application.port.in.FranchiseUseCase;
import org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto.BranchRequest;
import org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto.FranchiseRequest;
import org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto.FranchiseResponse;
import org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto.ProductNameRequest;
import org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto.ProductRequest;
import org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto.StockRequest;
import org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto.TopStockProductResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/franchises")
@Tag(name = "Franchises", description = "Operaciones para administrar franquicias, sucursales y productos.")
public class FranchiseController {

    private final FranchiseUseCase franchiseUseCase;

    public FranchiseController(FranchiseUseCase franchiseUseCase) {
        this.franchiseUseCase = franchiseUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crear una franquicia",
            description = "Crea una nueva franquicia sin sucursales.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Franquicia creada",
                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida")
            }
    )
    public Mono<FranchiseResponse> createFranchise(@RequestBody FranchiseRequest request) {
        return franchiseUseCase.createFranchise(request.name())
                .map(FranchiseResponse::fromDomain);
    }

    @GetMapping
    @Operation(
            summary = "Listar franquicias",
            description = "Retorna todas las franquicias registradas.",
            responses = @ApiResponse(responseCode = "200", description = "Listado de franquicias")
    )
    public Flux<FranchiseResponse> findAllFranchises() {
        return franchiseUseCase.findAllFranchises()
                .map(FranchiseResponse::fromDomain);
    }

    @GetMapping("/{franchiseId}")
    @Operation(
            summary = "Consultar franquicia por ID",
            description = "Retorna una franquicia específica con sus sucursales y productos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Franquicia encontrada",
                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Franquicia no encontrada")
            }
    )
    public Mono<FranchiseResponse> findFranchiseById(
            @Parameter(description = "ID de la franquicia", example = "f4f7a6f5-865d-4c6a-8b4a-23dbd21c8af1")
            @PathVariable String franchiseId
    ) {
        return franchiseUseCase.findFranchiseById(franchiseId)
                .map(FranchiseResponse::fromDomain);
    }

    @GetMapping("/{franchiseId}/top-stock-products")
    @Operation(
            summary = "Obtener productos con mayor stock por sucursal",
            description = "Retorna el producto con mayor stock de cada sucursal de una franquicia.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Productos encontrados"),
                    @ApiResponse(responseCode = "404", description = "Franquicia no encontrada")
            }
    )
    public Flux<TopStockProductResponse> findTopStockProductsByBranch(
            @Parameter(description = "ID de la franquicia", example = "f4f7a6f5-865d-4c6a-8b4a-23dbd21c8af1")
            @PathVariable String franchiseId
    ) {
        return franchiseUseCase.findTopStockProductsByBranch(franchiseId)
                .map(TopStockProductResponse::fromDomain);
    }

    @PatchMapping("/{franchiseId}")
    @Operation(
            summary = "Actualizar nombre de franquicia",
            description = "Modifica el nombre de una franquicia existente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Franquicia actualizada",
                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                    @ApiResponse(responseCode = "404", description = "Franquicia no encontrada")
            }
    )
    public Mono<FranchiseResponse> renameFranchise(
            @Parameter(description = "ID de la franquicia", example = "f4f7a6f5-865d-4c6a-8b4a-23dbd21c8af1")
            @PathVariable String franchiseId,
            @RequestBody FranchiseRequest request
    ) {
        return franchiseUseCase.renameFranchise(franchiseId, request.name())
                .map(FranchiseResponse::fromDomain);
    }

    @PostMapping("/{franchiseId}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Agregar sucursal",
            description = "Agrega una nueva sucursal a una franquicia existente.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Sucursal agregada",
                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                    @ApiResponse(responseCode = "404", description = "Franquicia no encontrada")
            }
    )
    public Mono<FranchiseResponse> addBranch(
            @Parameter(description = "ID de la franquicia", example = "f4f7a6f5-865d-4c6a-8b4a-23dbd21c8af1")
            @PathVariable String franchiseId,
            @RequestBody BranchRequest request
    ) {
        return franchiseUseCase.addBranch(franchiseId, request.name())
                .map(FranchiseResponse::fromDomain);
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}")
    @Operation(
            summary = "Actualizar nombre de sucursal",
            description = "Modifica el nombre de una sucursal de una franquicia.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucursal actualizada",
                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                    @ApiResponse(responseCode = "404", description = "Franquicia o sucursal no encontrada")
            }
    )
    public Mono<FranchiseResponse> renameBranch(
            @Parameter(description = "ID de la franquicia", example = "f4f7a6f5-865d-4c6a-8b4a-23dbd21c8af1")
            @PathVariable String franchiseId,
            @Parameter(description = "ID de la sucursal", example = "6b733aef-b0f6-46ed-a624-093be31a6387")
            @PathVariable String branchId,
            @RequestBody BranchRequest request
    ) {
        return franchiseUseCase.renameBranch(franchiseId, branchId, request.name())
                .map(FranchiseResponse::fromDomain);
    }

    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Agregar producto",
            description = "Agrega un nuevo producto a una sucursal de una franquicia.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Producto agregado",
                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                    @ApiResponse(responseCode = "404", description = "Franquicia o sucursal no encontrada")
            }
    )
    public Mono<FranchiseResponse> addProduct(
            @Parameter(description = "ID de la franquicia", example = "f4f7a6f5-865d-4c6a-8b4a-23dbd21c8af1")
            @PathVariable String franchiseId,
            @Parameter(description = "ID de la sucursal", example = "6b733aef-b0f6-46ed-a624-093be31a6387")
            @PathVariable String branchId,
            @RequestBody ProductRequest request
    ) {
        return franchiseUseCase.addProduct(franchiseId, branchId, request.name(), request.stock())
                .map(FranchiseResponse::fromDomain);
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    @Operation(
            summary = "Actualizar nombre de producto",
            description = "Modifica el nombre de un producto de una sucursal.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto actualizado",
                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                    @ApiResponse(responseCode = "404", description = "Franquicia, sucursal o producto no encontrado")
            }
    )
    public Mono<FranchiseResponse> renameProduct(
            @Parameter(description = "ID de la franquicia", example = "f4f7a6f5-865d-4c6a-8b4a-23dbd21c8af1")
            @PathVariable String franchiseId,
            @Parameter(description = "ID de la sucursal", example = "6b733aef-b0f6-46ed-a624-093be31a6387")
            @PathVariable String branchId,
            @Parameter(description = "ID del producto", example = "975f69e0-1979-4ce7-9708-ecb72f2a92ea")
            @PathVariable String productId,
            @RequestBody ProductNameRequest request
    ) {
        return franchiseUseCase.renameProduct(franchiseId, branchId, productId, request.name())
                .map(FranchiseResponse::fromDomain);
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/stock")
    @Operation(
            summary = "Actualizar stock de producto",
            description = "Modifica la cantidad de stock de un producto.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stock actualizado",
                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                    @ApiResponse(responseCode = "404", description = "Franquicia, sucursal o producto no encontrado")
            }
    )
    public Mono<FranchiseResponse> updateProductStock(
            @Parameter(description = "ID de la franquicia", example = "f4f7a6f5-865d-4c6a-8b4a-23dbd21c8af1")
            @PathVariable String franchiseId,
            @Parameter(description = "ID de la sucursal", example = "6b733aef-b0f6-46ed-a624-093be31a6387")
            @PathVariable String branchId,
            @Parameter(description = "ID del producto", example = "975f69e0-1979-4ce7-9708-ecb72f2a92ea")
            @PathVariable String productId,
            @RequestBody StockRequest request
    ) {
        return franchiseUseCase.updateProductStock(franchiseId, branchId, productId, request.stock())
                .map(FranchiseResponse::fromDomain);
    }

    @DeleteMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    @Operation(
            summary = "Eliminar producto",
            description = "Elimina un producto de una sucursal.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto eliminado",
                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Franquicia, sucursal o producto no encontrado")
            }
    )
    public Mono<FranchiseResponse> deleteProduct(
            @Parameter(description = "ID de la franquicia", example = "f4f7a6f5-865d-4c6a-8b4a-23dbd21c8af1")
            @PathVariable String franchiseId,
            @Parameter(description = "ID de la sucursal", example = "6b733aef-b0f6-46ed-a624-093be31a6387")
            @PathVariable String branchId,
            @Parameter(description = "ID del producto", example = "975f69e0-1979-4ce7-9708-ecb72f2a92ea")
            @PathVariable String productId
    ) {
        return franchiseUseCase.deleteProduct(franchiseId, branchId, productId)
                .map(FranchiseResponse::fromDomain);
    }

    @DeleteMapping("/{franchiseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Eliminar franquicia",
            description = "Elimina una franquicia por ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Franquicia eliminada",
                            content = @Content(examples = @ExampleObject(value = ""))),
                    @ApiResponse(responseCode = "404", description = "Franquicia no encontrada")
            }
    )
    public Mono<Void> deleteFranchise(
            @Parameter(description = "ID de la franquicia", example = "f4f7a6f5-865d-4c6a-8b4a-23dbd21c8af1")
            @PathVariable String franchiseId
    ) {
        return franchiseUseCase.deleteFranchise(franchiseId);
    }
}
