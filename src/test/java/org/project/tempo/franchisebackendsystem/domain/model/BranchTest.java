package org.project.tempo.franchisebackendsystem.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class BranchTest {

    @Test
    void shouldCreateBranchWithEmptyProductListWhenProductsAreNull() {
        Branch branch = new Branch("branch-1", "North", null);

        assertEquals("branch-1", branch.id());
        assertEquals("North", branch.name());
        assertEquals(0, branch.products().size());
    }

    @Test
    void shouldRejectInvalidBranch() {
        assertThrows(IllegalArgumentException.class, () -> new Branch(null, "North", null));
        assertThrows(IllegalArgumentException.class, () -> new Branch("branch-1", "", null));
    }

    @Test
    void shouldManageProductsImmutably() {
        Branch branch = new Branch("branch-1", "North", List.of(new Product("product-1", "Coffee", 10)));

        Branch renamed = branch.rename("South");
        Branch withProduct = branch.addProduct(new Product("product-2", "Tea", 5));
        Branch withUpdatedStock = withProduct.updateProductStock("product-1", 25);
        Branch withRenamedProduct = withUpdatedStock.renameProduct("product-2", "Green Tea");
        Branch withoutProduct = withRenamedProduct.removeProduct("product-1");

        assertEquals("South", renamed.name());
        assertEquals("North", branch.name());
        assertEquals(2, withProduct.products().size());
        assertEquals(25, withUpdatedStock.products().get(0).stock());
        assertEquals("Green Tea", withRenamedProduct.products().get(1).name());
        assertEquals(1, withoutProduct.products().size());
        assertEquals("product-2", withoutProduct.products().get(0).id());
    }
}
