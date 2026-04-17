package org.project.tempo.franchisebackendsystem.application.usecase;

import java.util.UUID;
import org.project.tempo.franchisebackendsystem.application.port.in.FranchiseUseCase;
import org.project.tempo.franchisebackendsystem.application.port.out.FranchiseRepositoryPort;
import org.project.tempo.franchisebackendsystem.domain.exception.EntityNotFoundException;
import org.project.tempo.franchisebackendsystem.domain.model.Branch;
import org.project.tempo.franchisebackendsystem.domain.model.Franchise;
import org.project.tempo.franchisebackendsystem.domain.model.Product;
import org.project.tempo.franchisebackendsystem.domain.model.TopStockProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FranchiseService implements FranchiseUseCase {

    private final FranchiseRepositoryPort franchiseRepositoryPort;

    public FranchiseService(FranchiseRepositoryPort franchiseRepositoryPort) {
        this.franchiseRepositoryPort = franchiseRepositoryPort;
    }

    @Override
    public Mono<Franchise> createFranchise(String name) {
        Franchise franchise = new Franchise(newId(), name, null);
        return franchiseRepositoryPort.save(franchise);
    }

    @Override
    public Mono<Franchise> findFranchiseById(String franchiseId) {
        return findExistingFranchise(franchiseId);
    }

    @Override
    public Flux<Franchise> findAllFranchises() {
        return franchiseRepositoryPort.findAll();
    }

    @Override
    public Mono<Franchise> renameFranchise(String franchiseId, String name) {
        return findExistingFranchise(franchiseId)
                .map(franchise -> franchise.rename(name))
                .flatMap(franchiseRepositoryPort::save);
    }

    @Override
    public Mono<Franchise> addBranch(String franchiseId, String branchName) {
        return findExistingFranchise(franchiseId)
                .map(franchise -> franchise.addBranch(new Branch(newId(), branchName, null)))
                .flatMap(franchiseRepositoryPort::save);
    }

    @Override
    public Mono<Franchise> renameBranch(String franchiseId, String branchId, String name) {
        return findExistingFranchise(franchiseId)
                .map(franchise -> {
                    assertBranchExists(franchise, branchId);
                    return franchise.renameBranch(branchId, name);
                })
                .flatMap(franchiseRepositoryPort::save);
    }

    @Override
    public Mono<Franchise> addProduct(String franchiseId, String branchId, String productName, int stock) {
        return findExistingFranchise(franchiseId)
                .map(franchise -> {
                    assertBranchExists(franchise, branchId);
                    return franchise.addProduct(branchId, new Product(newId(), productName, stock));
                })
                .flatMap(franchiseRepositoryPort::save);
    }

    @Override
    public Mono<Franchise> renameProduct(String franchiseId, String branchId, String productId, String name) {
        return findExistingFranchise(franchiseId)
                .map(franchise -> {
                    assertProductExists(franchise, branchId, productId);
                    return franchise.renameProduct(branchId, productId, name);
                })
                .flatMap(franchiseRepositoryPort::save);
    }

    @Override
    public Mono<Franchise> updateProductStock(String franchiseId, String branchId, String productId, int stock) {
        return findExistingFranchise(franchiseId)
                .map(franchise -> {
                    assertProductExists(franchise, branchId, productId);
                    return franchise.updateProductStock(branchId, productId, stock);
                })
                .flatMap(franchiseRepositoryPort::save);
    }

    @Override
    public Mono<Franchise> deleteProduct(String franchiseId, String branchId, String productId) {
        return findExistingFranchise(franchiseId)
                .map(franchise -> {
                    assertProductExists(franchise, branchId, productId);
                    return franchise.removeProduct(branchId, productId);
                })
                .flatMap(franchiseRepositoryPort::save);
    }

    @Override
    public Flux<TopStockProduct> findTopStockProductsByBranch(String franchiseId) {
        return findExistingFranchise(franchiseId)
                .flatMapMany(franchise -> Flux.fromIterable(franchise.branches()))
                .flatMap(branch -> Mono.justOrEmpty(branch.products().stream()
                        .max((left, right) -> Integer.compare(left.stock(), right.stock()))
                        .map(product -> new TopStockProduct(branch.id(), branch.name(), product))));
    }

    @Override
    public Mono<Void> deleteFranchise(String franchiseId) {
        return findExistingFranchise(franchiseId)
                .then(franchiseRepositoryPort.deleteById(franchiseId));
    }

    private Mono<Franchise> findExistingFranchise(String franchiseId) {
        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Franchise not found: " + franchiseId)));
    }

    private void assertBranchExists(Franchise franchise, String branchId) {
        boolean exists = franchise.branches().stream()
                .anyMatch(branch -> branch.id().equals(branchId));
        if (!exists) {
            throw new EntityNotFoundException("Branch not found: " + branchId);
        }
    }

    private void assertProductExists(Franchise franchise, String branchId, String productId) {
        Branch branch = franchise.branches().stream()
                .filter(candidate -> candidate.id().equals(branchId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Branch not found: " + branchId));

        boolean exists = branch.products().stream()
                .anyMatch(product -> product.id().equals(productId));
        if (!exists) {
            throw new EntityNotFoundException("Product not found: " + productId);
        }
    }

    private String newId() {
        return UUID.randomUUID().toString();
    }
}
