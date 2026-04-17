package org.project.tempo.franchisebackendsystem.domain.model;

public record Product(String id, String name, int stock) {

    public Product {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Product id is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative");
        }
    }

    public Product rename(String newName) {
        return new Product(id, newName, stock);
    }

    public Product updateStock(int newStock) {
        return new Product(id, name, newStock);
    }
}
