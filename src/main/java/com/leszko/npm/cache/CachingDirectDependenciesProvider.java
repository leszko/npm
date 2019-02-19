package com.leszko.npm.cache;

import com.leszko.npm.domain.DirectDependenciesProvider;
import com.leszko.npm.domain.PackageDefinition;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Direct dependencies provider which adds caching layer.
 * <p>
 * Note that this class follows the Decorator design pattern.
 */
public class CachingDirectDependenciesProvider
        implements DirectDependenciesProvider {

    private final DirectDependenciesProvider provider;
    private final Cache cache;

    public CachingDirectDependenciesProvider(DirectDependenciesProvider provider,
                                             CacheManager cacheManager) {
        this.provider = provider;
        this.cache = cacheManager.getCache("direct-dependencies");
    }

    /**
     * Looks up in the cache for the already requested package definition.
     * <p>
     * Note that the version 'latest' is not cached, because it may change in the registry.
     */
    @Override
    public CompletableFuture<List<PackageDefinition>> provide(PackageDefinition definition) {
        if ("latest".equals(definition.getVersion())) {
            return provider.provide(definition);
        }
        if (cache.containsKey(definition)) {
            return completedFuture((List<PackageDefinition>) cache.get(definition));
        } else {
            return provider.provide(definition).thenApplyAsync(result -> {
                cache.put(definition, result);
                return result;
            });
        }
    }
}
