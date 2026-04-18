package org.project.tempo.franchisebackendsystem.infrastructure.adapter.out.persistence.mongodb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.project.tempo.franchisebackendsystem.domain.model.Branch;
import org.project.tempo.franchisebackendsystem.domain.model.Franchise;
import org.project.tempo.franchisebackendsystem.domain.model.Product;
import org.project.tempo.franchisebackendsystem.infrastructure.adapter.out.persistence.mongodb.FranchiseDocument.BranchDocument;
import org.project.tempo.franchisebackendsystem.infrastructure.adapter.out.persistence.mongodb.FranchiseDocument.ProductDocument;

class FranchiseMongoMapperTest {

    private final FranchiseMongoMapper mapper = new FranchiseMongoMapper();

    @Test
    void shouldMapDomainToDocument() {
        Franchise franchise = new Franchise(
                "franchise-1",
                "Franchise",
                List.of(new Branch("branch-1", "North", List.of(new Product("product-1", "Coffee", 10))))
        );

        FranchiseDocument document = mapper.toDocument(franchise);

        assertEquals("franchise-1", document.getId());
        assertEquals("Franchise", document.getName());
        assertEquals("branch-1", document.getBranches().get(0).getId());
        assertEquals("Coffee", document.getBranches().get(0).getProducts().get(0).getName());
        assertEquals(10, document.getBranches().get(0).getProducts().get(0).getStock());
    }

    @Test
    void shouldMapDocumentToDomain() {
        FranchiseDocument document = new FranchiseDocument(
                "franchise-1",
                "Franchise",
                List.of(new BranchDocument(
                        "branch-1",
                        "North",
                        List.of(new ProductDocument("product-1", "Coffee", 10))
                ))
        );

        Franchise franchise = mapper.toDomain(document);

        assertEquals("franchise-1", franchise.id());
        assertEquals("Franchise", franchise.name());
        assertEquals("branch-1", franchise.branches().get(0).id());
        assertEquals("Coffee", franchise.branches().get(0).products().get(0).name());
        assertEquals(10, franchise.branches().get(0).products().get(0).stock());
    }

    @Test
    void shouldMapNullDocumentListsToEmptyDomainLists() {
        FranchiseDocument document = new FranchiseDocument("franchise-1", "Franchise", null);

        Franchise franchise = mapper.toDomain(document);

        assertEquals(0, franchise.branches().size());
    }
}
