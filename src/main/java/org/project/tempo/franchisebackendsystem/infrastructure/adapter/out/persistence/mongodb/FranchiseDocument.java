package org.project.tempo.franchisebackendsystem.infrastructure.adapter.out.persistence.mongodb;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "franchises")
public class FranchiseDocument {

    @Id
    private String id;
    private String name;
    private List<BranchDocument> branches = new ArrayList<>();

    public FranchiseDocument() {
    }

    public FranchiseDocument(String id, String name, List<BranchDocument> branches) {
        this.id = id;
        this.name = name;
        this.branches = branches;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BranchDocument> getBranches() {
        return branches;
    }

    public void setBranches(List<BranchDocument> branches) {
        this.branches = branches;
    }

    public static class BranchDocument {

        private String id;
        private String name;
        private List<ProductDocument> products = new ArrayList<>();

        public BranchDocument() {
        }

        public BranchDocument(String id, String name, List<ProductDocument> products) {
            this.id = id;
            this.name = name;
            this.products = products;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<ProductDocument> getProducts() {
            return products;
        }

        public void setProducts(List<ProductDocument> products) {
            this.products = products;
        }
    }

    public static class ProductDocument {

        private String id;
        private String name;
        private int stock;

        public ProductDocument() {
        }

        public ProductDocument(String id, String name, int stock) {
            this.id = id;
            this.name = name;
            this.stock = stock;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }
    }
}
