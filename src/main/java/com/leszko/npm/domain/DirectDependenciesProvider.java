package com.leszko.npm.domain;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Provides direct dependencies for the given NPM package in the asynchronous manner.
 */
public interface DirectDependenciesProvider {
    CompletableFuture<List<PackageDefinition>> provide(PackageDefinition definition);
}
