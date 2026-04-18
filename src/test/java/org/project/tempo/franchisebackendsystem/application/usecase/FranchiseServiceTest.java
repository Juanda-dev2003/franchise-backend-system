package org.project.tempo.franchisebackendsystem.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.tempo.franchisebackendsystem.application.port.out.FranchiseRepositoryPort;
import org.project.tempo.franchisebackendsystem.domain.exception.EntityNotFoundException;
import org.project.tempo.franchisebackendsystem.domain.model.Branch;
import org.project.tempo.franchisebackendsystem.domain.model.Franchise;
import org.project.tempo.franchisebackendsystem.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FranchiseServiceTest {

    private InMemoryFranchiseRepository repository;
    private FranchiseService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryFranchiseRepository();
        service = new FranchiseService(repository);
    }

    @Test
    void shouldCreateFranchise() {
        StepVerifier.create(service.createFranchise("Franquicia Centro"))
                .assertNext(franchise -> {
                    assertEquals("Franquicia Centro", franchise.name());
                    assertEquals(0, franchise.branches().size());
                    assertEquals(franchise, repository.savedFranchise);
                })
                .verifyComplete();
    }

    @Test
    void shouldFindFranchiseByIdAndFindAll() {
        Franchise franchise = seed(franchiseWithProducts());

        StepVerifier.create(service.findFranchiseById(franchise.id()))
                .expectNext(franchise)
                .verifyComplete();

        StepVerifier.create(service.findAllFranchises())
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void shouldRenameFranchise() {
        Franchise franchise = seed(franchiseWithProducts());

        StepVerifier.create(service.renameFranchise(franchise.id(), "Nuevo Nombre"))
                .assertNext(updated -> assertEquals("Nuevo Nombre", updated.name()))
                .verifyComplete();
    }

    @Test
    void shouldAddAndRenameBranch() {
        Franchise franchise = seed(new Franchise("franchise-1", "Franchise", null));

        Franchise updated = service.addBranch(franchise.id(), "Sucursal Norte").block();
        String branchId = updated.branches().get(0).id();

        StepVerifier.create(service.renameBranch(franchise.id(), branchId, "Sucursal Sur"))
                .assertNext(result -> assertEquals("Sucursal Sur", result.branches().get(0).name()))
                .verifyComplete();
    }

    @Test
    void shouldAddRenameUpdateAndDeleteProduct() {
        Franchise franchise = seed(new Franchise(
                "franchise-1",
                "Franchise",
                List.of(new Branch("branch-1", "Sucursal", null))
        ));

        Franchise withProduct = service.addProduct(franchise.id(), "branch-1", "Cafe", 10).block();
        String productId = withProduct.branches().get(0).products().get(0).id();

        StepVerifier.create(service.renameProduct(franchise.id(), "branch-1", productId, "Cafe Premium"))
                .assertNext(result -> assertEquals("Cafe Premium", result.branches().get(0).products().get(0).name()))
                .verifyComplete();

        StepVerifier.create(service.updateProductStock(franchise.id(), "branch-1", productId, 35))
                .assertNext(result -> assertEquals(35, result.branches().get(0).products().get(0).stock()))
                .verifyComplete();

        StepVerifier.create(service.deleteProduct(franchise.id(), "branch-1", productId))
                .assertNext(result -> assertEquals(0, result.branches().get(0).products().size()))
                .verifyComplete();
    }

    @Test
    void shouldFindTopStockProductByBranch() {
        Franchise franchise = seed(franchiseWithProducts());

        StepVerifier.create(service.findTopStockProductsByBranch(franchise.id()))
                .assertNext(topStockProduct -> {
                    assertEquals("branch-1", topStockProduct.branchId());
                    assertEquals("North", topStockProduct.branchName());
                    assertEquals("Tea", topStockProduct.product().name());
                    assertEquals(40, topStockProduct.product().stock());
                })
                .assertNext(topStockProduct -> {
                    assertEquals("branch-2", topStockProduct.branchId());
                    assertEquals("South", topStockProduct.branchName());
                    assertEquals("Juice", topStockProduct.product().name());
                    assertEquals(12, topStockProduct.product().stock());
                })
                .verifyComplete();
    }

    @Test
    void shouldIgnoreBranchesWithoutProductsWhenFindingTopStockProducts() {
        Franchise franchise = seed(new Franchise(
                "franchise-1",
                "Franchise",
                List.of(new Branch("branch-1", "Empty", null))
        ));

        StepVerifier.create(service.findTopStockProductsByBranch(franchise.id()))
                .verifyComplete();
    }

    @Test
    void shouldDeleteFranchise() {
        Franchise franchise = seed(franchiseWithProducts());

        StepVerifier.create(service.deleteFranchise(franchise.id()))
                .verifyComplete();

        StepVerifier.create(service.findFranchiseById(franchise.id()))
                .expectError(EntityNotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnErrorWhenEntitiesDoNotExist() {
        StepVerifier.create(service.findFranchiseById("missing"))
                .expectError(EntityNotFoundException.class)
                .verify();

        Franchise franchise = seed(new Franchise("franchise-1", "Franchise", null));

        StepVerifier.create(service.addProduct(franchise.id(), "missing-branch", "Cafe", 10))
                .expectError(EntityNotFoundException.class)
                .verify();

        StepVerifier.create(service.updateProductStock(franchise.id(), "missing-branch", "product-1", 10))
                .expectError(EntityNotFoundException.class)
                .verify();

        Franchise withBranch = seed(new Franchise(
                "franchise-1",
                "Franchise",
                List.of(new Branch("branch-1", "Sucursal", null))
        ));

        StepVerifier.create(service.renameProduct(withBranch.id(), "branch-1", "missing-product", "Cafe"))
                .expectError(EntityNotFoundException.class)
                .verify();
    }

    private Franchise seed(Franchise franchise) {
        repository.store.put(franchise.id(), franchise);
        return franchise;
    }

    private Franchise franchiseWithProducts() {
        return new Franchise(
                "franchise-1",
                "Franchise",
                List.of(
                        new Branch("branch-1", "North", List.of(
                                new Product("product-1", "Coffee", 10),
                                new Product("product-2", "Tea", 40)
                        )),
                        new Branch("branch-2", "South", List.of(
                                new Product("product-3", "Juice", 12)
                        ))
                )
        );
    }

    private static class InMemoryFranchiseRepository implements FranchiseRepositoryPort {

        private final Map<String, Franchise> store = new LinkedHashMap<>();
        private Franchise savedFranchise;

        @Override
        public Mono<Franchise> save(Franchise franchise) {
            store.put(franchise.id(), franchise);
            savedFranchise = franchise;
            return Mono.just(franchise);
        }

        @Override
        public Mono<Franchise> findById(String franchiseId) {
            return Mono.justOrEmpty(store.get(franchiseId));
        }

        @Override
        public Flux<Franchise> findAll() {
            return Flux.fromIterable(store.values());
        }

        @Override
        public Mono<Void> deleteById(String franchiseId) {
            store.remove(franchiseId);
            return Mono.empty();
        }
    }
}
