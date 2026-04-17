package org.project.tempo.franchisebackendsystem.infrastructure.adapter.out.persistence.mongodb;

import org.project.tempo.franchisebackendsystem.application.port.out.FranchiseRepositoryPort;
import org.project.tempo.franchisebackendsystem.domain.model.Franchise;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class FranchiseMongoAdapter implements FranchiseRepositoryPort {

    private final ReactiveMongoFranchiseRepository franchiseRepository;
    private final FranchiseMongoMapper franchiseMongoMapper;

    public FranchiseMongoAdapter(
            ReactiveMongoFranchiseRepository franchiseRepository,
            FranchiseMongoMapper franchiseMongoMapper
    ) {
        this.franchiseRepository = franchiseRepository;
        this.franchiseMongoMapper = franchiseMongoMapper;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return franchiseRepository.save(franchiseMongoMapper.toDocument(franchise))
                .map(franchiseMongoMapper::toDomain);
    }

    @Override
    public Mono<Franchise> findById(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .map(franchiseMongoMapper::toDomain);
    }

    @Override
    public Flux<Franchise> findAll() {
        return franchiseRepository.findAll()
                .map(franchiseMongoMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String franchiseId) {
        return franchiseRepository.deleteById(franchiseId);
    }
}
