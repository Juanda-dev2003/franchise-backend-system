package org.project.tempo.franchisebackendsystem.domain.model;

import java.util.ArrayList;
import java.util.List;

public record Franchise(String id, String name, List<Branch> branches) {

    public Franchise {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Franchise id is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Franchise name is required");
        }
        branches = branches == null ? List.of() : List.copyOf(branches);
    }

    public Franchise rename(String newName) {
        return new Franchise(id, newName, branches);
    }

    public Franchise addBranch(Branch branch) {
        List<Branch> updatedBranches = new ArrayList<>(branches);
        updatedBranches.add(branch);
        return new Franchise(id, name, updatedBranches);
    }

    public Franchise addProduct(String branchId, Product product) {
        return replaceBranch(branchId, branch -> branch.addProduct(product));
    }

    public Franchise updateProductStock(String branchId, String productId, int stock) {
        return replaceBranch(branchId, branch -> branch.updateProductStock(productId, stock));
    }

    public Franchise renameBranch(String branchId, String newName) {
        return replaceBranch(branchId, branch -> branch.rename(newName));
    }

    public Franchise renameProduct(String branchId, String productId, String newName) {
        return replaceBranch(branchId, branch -> branch.renameProduct(productId, newName));
    }

    public Franchise removeProduct(String branchId, String productId) {
        return replaceBranch(branchId, branch -> branch.removeProduct(productId));
    }

    private Franchise replaceBranch(String branchId, BranchReplacement replacement) {
        List<Branch> updatedBranches = branches.stream()
                .map(branch -> branch.id().equals(branchId) ? replacement.apply(branch) : branch)
                .toList();
        return new Franchise(id, name, updatedBranches);
    }

    @FunctionalInterface
    private interface BranchReplacement {
        Branch apply(Branch branch);
    }
}
