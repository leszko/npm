package com.leszko.npm.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Async service with the business logic to resolve dependencies for the given NPM package.
 */
@Service
public final class DependenciesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DependenciesService.class);

    private final DirectDependenciesProvider directDependenciesProvider;

    public DependenciesService(DirectDependenciesProvider directDependenciesProvider) {
        this.directDependenciesProvider = directDependenciesProvider;
    }

    /**
     * Resolves dependencies for the given package definition.
     */
    public CompletableFuture<Package> resolveDependencies(PackageDefinition definition) {
        LOGGER.info("Processing package '{}:{}'", definition.getName(), definition.getVersion());
        return resolveDependenciesRecursively(definition);
    }

    private CompletableFuture<Package> resolveDependenciesRecursively(PackageDefinition definition) {
        LOGGER.debug("Resolving dependencies for package '{}:{}'", definition.getName(), definition.getVersion());

        return directDependenciesProvider.provide(definition).thenApplyAsync(dependencies -> {
            List<CompletableFuture<Package>> futures = new ArrayList<>();
            dependencies.forEach(dependency -> {
                futures.add(resolveDependenciesRecursively(dependency));
            });
            List<Package> packages = futures.stream().map(future -> future.join()).collect(Collectors.toList());
            return Package.builder().definition(definition).dependencies(packages).build();
        });
    }
}
