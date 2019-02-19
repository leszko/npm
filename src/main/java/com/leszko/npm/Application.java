package com.leszko.npm;

import com.hazelcast.cache.impl.HazelcastServerCachingProvider;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.leszko.npm.cache.CachingDirectDependenciesProvider;
import com.leszko.npm.domain.DirectDependenciesProvider;
import com.leszko.npm.npmjsregistry.NpmjsRegistryClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

import javax.cache.CacheManager;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    DirectDependenciesProvider dependenciesProvider(RestTemplateBuilder restTemplateBuilder, CacheManager cacheManager) {
        return new CachingDirectDependenciesProvider(new NpmjsRegistryClient(restTemplateBuilder), cacheManager);
    }

    @Bean
    CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return HazelcastServerCachingProvider.createCachingProvider(hazelcastInstance).getCacheManager();
    }

    @Bean
    public Config hazelcastConfig() {
        Config config = new Config();
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.addCacheConfig(new CacheSimpleConfig().setName("direct-dependencies"));
        return config;
    }
}

