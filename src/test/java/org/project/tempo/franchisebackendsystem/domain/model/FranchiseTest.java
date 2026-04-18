package org.project.tempo.franchisebackendsystem.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class FranchiseTest {

    @Test
    void shouldCreateFranchiseWithEmptyBranchListWhenBranchesAreNull() {
        Franchise franchise = new Franchise("franchise-1", "Main Franchise", null);

        assertEquals("franchise-1", franchise.id());
        assertEquals("Main Franchise", franchise.name());
        assertEquals(0, franchise.branches().size());
    }

    @Test
    void shouldRejectInvalidFranchise() {
        assertThrows(IllegalArgumentException.class, () -> new Franchise(null, "Main Franchise", null));
        assertThrows(IllegalArgumentException.class, () -> new Franchise("franchise-1", " ", null));
    }

    @Test
    void shouldManageBranchesAndProductsImmutably() {
        Branch branch = new Branch("branch-1", "North", List.of(new Product("product-1", "Coffee", 10)));
        Franchise franchise = new Franchise("franchise-1", "Main Franchise", List.of(branch));

        Franchise renamed = franchise.rename("Updated Franchise");
        Franchise withBranch = franchise.addBranch(new Branch("branch-2", "South", null));
        Franchise withRenamedBranch = withBranch.renameBranch("branch-2", "East");
        Franchise withProduct = withRenamedBranch.addProduct("branch-2", new Product("product-2", "Tea", 8));
        Franchise withUpdatedStock = withProduct.updateProductStock("branch-2", "product-2", 18);
        Franchise withRenamedProduct = withUpdatedStock.renameProduct("branch-2", "product-2", "Green Tea");
        Franchise withoutProduct = withRenamedProduct.removeProduct("branch-2", "product-2");

        assertEquals("Updated Franchise", renamed.name());
        assertEquals("Main Franchise", franchise.name());
        assertEquals(2, withBranch.branches().size());
        assertEquals("East", withRenamedBranch.branches().get(1).name());
        assertEquals(1, withProduct.branches().get(1).products().size());
        assertEquals(18, withUpdatedStock.branches().get(1).products().get(0).stock());
        assertEquals("Green Tea", withRenamedProduct.branches().get(1).products().get(0).name());
        assertEquals(0, withoutProduct.branches().get(1).products().size());
    }
}
