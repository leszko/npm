package com.leszko.npm;

import com.leszko.npm.domain.DirectDependenciesProvider;
import com.leszko.npm.npmjsregistry.NpmjsRegistryClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    DirectDependenciesProvider dependenciesProvider(RestTemplateBuilder restTemplateBuilder) {
        return new NpmjsRegistryClient(restTemplateBuilder);
    }
}

