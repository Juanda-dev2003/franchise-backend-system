package org.project.tempo.franchisebackendsystem.infrastructure.adapter.out.persistence.mongodb;

import java.util.List;
import org.project.tempo.franchisebackendsystem.domain.model.Branch;
import org.project.tempo.franchisebackendsystem.domain.model.Franchise;
import org.project.tempo.franchisebackendsystem.domain.model.Product;
import org.project.tempo.franchisebackendsystem.infrastructure.adapter.out.persistence.mongodb.FranchiseDocument.BranchDocument;
import org.project.tempo.franchisebackendsystem.infrastructure.adapter.out.persistence.mongodb.FranchiseDocument.ProductDocument;
import org.springframework.stereotype.Component;

@Component
public class FranchiseMongoMapper {

    public FranchiseDocument toDocument(Franchise franchise) {
        return new FranchiseDocument(
                franchise.id(),
                franchise.name(),
                franchise.branches().stream().map(this::toBranchDocument).toList()
        );
    }

    public Franchise toDomain(FranchiseDocument document) {
        List<Branch> branches = safeList(document.getBranches()).stream()
                .map(this::toBranch)
                .toList();
        return new Franchise(document.getId(), document.getName(), branches);
    }

    private BranchDocument toBranchDocument(Branch branch) {
        return new BranchDocument(
                branch.id(),
                branch.name(),
                branch.products().stream().map(this::toProductDocument).toList()
        );
    }

    private ProductDocument toProductDocument(Product product) {
        return new ProductDocument(product.id(), product.name(), product.stock());
    }

    private Branch toBranch(BranchDocument document) {
        List<Product> products = safeList(document.getProducts()).stream()
                .map(this::toProduct)
                .toList();
        return new Branch(document.getId(), document.getName(), products);
    }

    private Product toProduct(ProductDocument document) {
        return new Product(document.getId(), document.getName(), document.getStock());
    }

    private <T> List<T> safeList(List<T> values) {
        return values == null ? List.of() : values;
    }
}
