package org.project.tempo.franchisebackendsystem.application.port.in;

import org.project.tempo.franchisebackendsystem.domain.model.Franchise;
import org.project.tempo.franchisebackendsystem.domain.model.TopStockProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseUseCase {

    Mono<Franchise> createFranchise(String name);

    Mono<Franchise> findFranchiseById(String franchiseId);

    Flux<Franchise> findAllFranchises();

    Mono<Franchise> renameFranchise(String franchiseId, String name);

    Mono<Franchise> addBranch(String franchiseId, String branchName);

    Mono<Franchise> renameBranch(String franchiseId, String branchId, String name);

    Mono<Franchise> addProduct(String franchiseId, String branchId, String productName, int stock);

    Mono<Franchise> renameProduct(String franchiseId, String branchId, String productId, String name);

    Mono<Franchise> updateProductStock(String franchiseId, String branchId, String productId, int stock);

    Mono<Franchise> deleteProduct(String franchiseId, String branchId, String productId);

    Flux<TopStockProduct> findTopStockProductsByBranch(String franchiseId);

    Mono<Void> deleteFranchise(String franchiseId);
}
