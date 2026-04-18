package org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.tempo.franchisebackendsystem.application.port.in.FranchiseUseCase;
import org.project.tempo.franchisebackendsystem.domain.exception.EntityNotFoundException;
import org.project.tempo.franchisebackendsystem.domain.model.Branch;
import org.project.tempo.franchisebackendsystem.domain.model.Franchise;
import org.project.tempo.franchisebackendsystem.domain.model.Product;
import org.project.tempo.franchisebackendsystem.domain.model.TopStockProduct;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class FranchiseControllerTest {

    private StubFranchiseUseCase useCase;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        useCase = new StubFranchiseUseCase();
        webTestClient = WebTestClient
                .bindToController(new FranchiseController(useCase))
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreateFranchise() {
        webTestClient.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Franchise\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("franchise-1")
                .jsonPath("$.name").isEqualTo("Franchise");
    }

    @Test
    void shouldFindAllFranchises() {
        webTestClient.get()
                .uri("/api/franchises")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("franchise-1");
    }

    @Test
    void shouldFindFranchiseById() {
        webTestClient.get()
                .uri("/api/franchises/franchise-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.branches[0].products[0].name").isEqualTo("Coffee");
    }

    @Test
    void shouldReturnTopStockProducts() {
        webTestClient.get()
                .uri("/api/franchises/franchise-1/top-stock-products")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].branchName").isEqualTo("North")
                .jsonPath("$[0].product.name").isEqualTo("Coffee");
    }

    @Test
    void shouldUpdateResources() {
        webTestClient.patch()
                .uri("/api/franchises/franchise-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Updated Franchise\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Updated Franchise");

        webTestClient.patch()
                .uri("/api/franchises/franchise-1/branches/branch-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"South\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.branches[0].name").isEqualTo("South");

        webTestClient.patch()
                .uri("/api/franchises/franchise-1/branches/branch-1/products/product-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Premium Coffee\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.branches[0].products[0].name").isEqualTo("Premium Coffee");

        webTestClient.patch()
                .uri("/api/franchises/franchise-1/branches/branch-1/products/product-1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"stock\":30}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.branches[0].products[0].stock").isEqualTo(30);
    }

    @Test
    void shouldAddBranchAndProductAndDeleteProduct() {
        webTestClient.post()
                .uri("/api/franchises/franchise-1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"South\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.branches[0].name").isEqualTo("South");

        webTestClient.post()
                .uri("/api/franchises/franchise-1/branches/branch-1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Tea\",\"stock\":12}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.branches[0].products[0].name").isEqualTo("Tea");

        webTestClient.delete()
                .uri("/api/franchises/franchise-1/branches/branch-1/products/product-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.branches[0].products").isEmpty();
    }

    @Test
    void shouldDeleteFranchise() {
        webTestClient.delete()
                .uri("/api/franchises/franchise-1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldMapErrors() {
        useCase.failWithNotFound = true;

        webTestClient.get()
                .uri("/api/franchises/missing")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Franchise not found: missing");
    }

    @Test
    void shouldRejectInvalidRequestBody() {
        webTestClient.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"\"}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid request body");
    }

    private static class StubFranchiseUseCase implements FranchiseUseCase {

        private boolean failWithNotFound;

        @Override
        public Mono<Franchise> createFranchise(String name) {
            return Mono.just(new Franchise("franchise-1", name, null));
        }

        @Override
        public Mono<Franchise> findFranchiseById(String franchiseId) {
            if (failWithNotFound) {
                return Mono.error(new EntityNotFoundException("Franchise not found: " + franchiseId));
            }
            return Mono.just(defaultFranchise());
        }

        @Override
        public Flux<Franchise> findAllFranchises() {
            return Flux.just(defaultFranchise());
        }

        @Override
        public Mono<Franchise> renameFranchise(String franchiseId, String name) {
            return Mono.just(new Franchise(franchiseId, name, null));
        }

        @Override
        public Mono<Franchise> addBranch(String franchiseId, String branchName) {
            return Mono.just(new Franchise(franchiseId, "Franchise", List.of(new Branch("branch-1", branchName, null))));
        }

        @Override
        public Mono<Franchise> renameBranch(String franchiseId, String branchId, String name) {
            return Mono.just(new Franchise(franchiseId, "Franchise", List.of(new Branch(branchId, name, null))));
        }

        @Override
        public Mono<Franchise> addProduct(String franchiseId, String branchId, String productName, int stock) {
            return Mono.just(new Franchise(
                    franchiseId,
                    "Franchise",
                    List.of(new Branch(branchId, "North", List.of(new Product("product-1", productName, stock))))
            ));
        }

        @Override
        public Mono<Franchise> renameProduct(String franchiseId, String branchId, String productId, String name) {
            return Mono.just(new Franchise(
                    franchiseId,
                    "Franchise",
                    List.of(new Branch(branchId, "North", List.of(new Product(productId, name, 10))))
            ));
        }

        @Override
        public Mono<Franchise> updateProductStock(String franchiseId, String branchId, String productId, int stock) {
            return Mono.just(new Franchise(
                    franchiseId,
                    "Franchise",
                    List.of(new Branch(branchId, "North", List.of(new Product(productId, "Coffee", stock))))
            ));
        }

        @Override
        public Mono<Franchise> deleteProduct(String franchiseId, String branchId, String productId) {
            return Mono.just(new Franchise(franchiseId, "Franchise", List.of(new Branch(branchId, "North", null))));
        }

        @Override
        public Flux<TopStockProduct> findTopStockProductsByBranch(String franchiseId) {
            Product product = new Product("product-1", "Coffee", 10);
            return Flux.just(new TopStockProduct("branch-1", "North", product));
        }

        @Override
        public Mono<Void> deleteFranchise(String franchiseId) {
            return Mono.empty();
        }

        private Franchise defaultFranchise() {
            return new Franchise(
                    "franchise-1",
                    "Franchise",
                    List.of(new Branch("branch-1", "North", List.of(new Product("product-1", "Coffee", 10))))
            );
        }
    }
}
