package org.project.tempo.franchisebackendsystem.infrastructure.config;

import org.project.tempo.franchisebackendsystem.application.port.in.FranchiseUseCase;
import org.project.tempo.franchisebackendsystem.application.port.out.FranchiseRepositoryPort;
import org.project.tempo.franchisebackendsystem.application.usecase.FranchiseService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {

    @Bean
    public FranchiseUseCase franchiseUseCase(FranchiseRepositoryPort franchiseRepositoryPort) {
        return new FranchiseService(franchiseRepositoryPort);
    }
}
