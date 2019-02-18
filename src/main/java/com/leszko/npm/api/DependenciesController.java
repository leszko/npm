package com.leszko.npm.api;

import com.leszko.npm.domain.Package;
import com.leszko.npm.domain.PackageDefinition;
import com.leszko.npm.domain.DependenciesService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * API Endpoint to provide NPM dependencies.
 */
@RestController
final class DependenciesController {
    private final DependenciesService dependenciesService;

    DependenciesController(DependenciesService dependenciesService) {
        this.dependenciesService = dependenciesService;
    }

    @RequestMapping(value = "/{packageName}/{versionOrTag}", produces = "application/json")
    DependenciesResponse dependencies(@PathVariable("packageName") String packageName,
                                      @PathVariable("versionOrTag") String versionOrTag)
            throws ExecutionException, InterruptedException {
        return toResponse(dependenciesService.resolveDependencies(toDefinition(packageName, versionOrTag)).get());
    }

    private static PackageDefinition toDefinition(String packageName, String versionOrTag) {
        return PackageDefinition.builder().name(packageName).version(versionOrTag).build();
    }

    private static DependenciesResponse toResponse(Package pack) {
        List<DependenciesResponse> dependencies = pack.getDependencies().stream()
                                                      .map(dep -> toResponse(dep))
                                                      .collect(Collectors.toList());
        return DependenciesResponse.builder()
                                   .name(pack.getDefinition().getName())
                                   .version(pack.getDefinition().getVersion())
                                   .dependencies(dependencies)
                                   .build();
    }
}
