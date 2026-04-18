package org.project.tempo.franchisebackendsystem.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void shouldCreateProduct() {
        Product product = new Product("product-1", "Coffee", 10);

        assertEquals("product-1", product.id());
        assertEquals("Coffee", product.name());
        assertEquals(10, product.stock());
    }

    @Test
    void shouldRejectInvalidProduct() {
        assertThrows(IllegalArgumentException.class, () -> new Product(null, "Coffee", 10));
        assertThrows(IllegalArgumentException.class, () -> new Product("product-1", " ", 10));
        assertThrows(IllegalArgumentException.class, () -> new Product("product-1", "Coffee", -1));
    }

    @Test
    void shouldRenameProductAndUpdateStock() {
        Product product = new Product("product-1", "Coffee", 10);

        Product renamed = product.rename("Premium Coffee");
        Product updatedStock = product.updateStock(25);

        assertEquals("Premium Coffee", renamed.name());
        assertEquals(10, renamed.stock());
        assertEquals("Coffee", updatedStock.name());
        assertEquals(25, updatedStock.stock());
    }
}
