package org.project.tempo.franchisebackendsystem.infrastructure.config;

import org.project.tempo.franchisebackendsystem.infrastructure.adapter.out.persistence.mongodb.FranchiseDocument;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@Configuration
public class MongoIndexConfiguration {

    @Bean
    public ApplicationRunner createMongoIndexes(ReactiveMongoTemplate mongoTemplate) {
        return args -> mongoTemplate.indexOps(FranchiseDocument.class)
                .createIndex(new Index().on("name", Sort.Direction.ASC).named("idx_franchise_name"))
                .then(mongoTemplate.indexOps(FranchiseDocument.class)
                        .createIndex(new Index().on("branches.id", Sort.Direction.ASC).named("idx_branch_id")))
                .then(mongoTemplate.indexOps(FranchiseDocument.class)
                        .createIndex(new Index().on("branches.name", Sort.Direction.ASC).named("idx_branch_name")))
                .then(mongoTemplate.indexOps(FranchiseDocument.class)
                        .createIndex(new Index().on("branches.products.id", Sort.Direction.ASC).named("idx_product_id")))
                .then(mongoTemplate.indexOps(FranchiseDocument.class)
                        .createIndex(new Index().on("branches.products.name", Sort.Direction.ASC).named("idx_product_name")))
                .subscribe();
    }
}
