package org.project.tempo.franchisebackendsystem.infrastructure.adapter.out.persistence.mongodb;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ReactiveMongoFranchiseRepository extends ReactiveMongoRepository<FranchiseDocument, String> {
}
