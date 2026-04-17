package org.project.tempo.franchisebackendsystem.application.port.out;

import org.project.tempo.franchisebackendsystem.domain.model.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseRepositoryPort {

    Mono<Franchise> save(Franchise franchise);

    Mono<Franchise> findById(String franchiseId);

    Flux<Franchise> findAll();

    Mono<Void> deleteById(String franchiseId);
}
