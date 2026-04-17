package org.project.tempo.franchisebackendsystem.domain.model;

import java.util.ArrayList;
import java.util.List;

public record Branch(String id, String name, List<Product> products) {

    public Branch {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Branch id is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Branch name is required");
        }
        products = products == null ? List.of() : List.copyOf(products);
    }

    public Branch rename(String newName) {
        return new Branch(id, newName, products);
    }

    public Branch addProduct(Product product) {
        List<Product> updatedProducts = new ArrayList<>(products);
        updatedProducts.add(product);
        return new Branch(id, name, updatedProducts);
    }

    public Branch updateProductStock(String productId, int stock) {
        return replaceProduct(productId, product -> product.updateStock(stock));
    }

    public Branch renameProduct(String productId, String newName) {
        return replaceProduct(productId, product -> product.rename(newName));
    }

    public Branch removeProduct(String productId) {
        List<Product> updatedProducts = products.stream()
                .filter(product -> !product.id().equals(productId))
                .toList();
        return new Branch(id, name, updatedProducts);
    }

    private Branch replaceProduct(String productId, ProductReplacement replacement) {
        List<Product> updatedProducts = products.stream()
                .map(product -> product.id().equals(productId) ? replacement.apply(product) : product)
                .toList();
        return new Branch(id, name, updatedProducts);
    }

    @FunctionalInterface
    private interface ProductReplacement {
        Product apply(Product product);
    }
}
