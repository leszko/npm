package com.leszko.npm.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class DependenciesServiceTest {
    @Mock
    private DirectDependenciesProvider directDependenciesProvider;

    @InjectMocks
    private DependenciesService service;

    @Test
    public void resolveDependencies()
            throws ExecutionException, InterruptedException {
        // given
        PackageDefinition parent = createPackageDefinition("parent", "1.0.1");
        PackageDefinition child1 = createPackageDefinition("child1", "1.2.3");
        PackageDefinition child2 = createPackageDefinition("child2", "1.2.4");
        PackageDefinition child11 = createPackageDefinition("child11", "1.2.5");
        given(directDependenciesProvider.provide(parent)).willReturn(completedFuture(asList(child1, child2)));
        given(directDependenciesProvider.provide(child1)).willReturn(completedFuture(asList(child11)));
        given(directDependenciesProvider.provide(child2)).willReturn(completedFuture(emptyList()));
        given(directDependenciesProvider.provide(child11)).willReturn(completedFuture(emptyList()));

        // when
        Package result = service.resolveDependencies(parent).get();

        // then
        Package expectedChild11 = createPackage(child11, emptyList());
        Package expectedChild1 = createPackage(child1, asList(expectedChild11));
        Package expectedChild2 = createPackage(child2, emptyList());
        Package expectedParent = createPackage(parent, asList(expectedChild1, expectedChild2));
        assertThat(result).isEqualTo(expectedParent);
    }

    @Test(expected = ExecutionException.class)
    public void resolveDependenciesThrowsException()
            throws ExecutionException, InterruptedException {
        // given
        PackageDefinition parent = createPackageDefinition("parent", "1.0.1");
        PackageDefinition child1 = createPackageDefinition("child1", "1.2.3");
        given(directDependenciesProvider.provide(parent)).willReturn(completedFuture(asList(child1)));
        given(directDependenciesProvider.provide(child1)).willThrow(new RuntimeException("Internal Server Error"));

        // when
        service.resolveDependencies(parent).get();
    }

    private static PackageDefinition createPackageDefinition(String name, String version) {
        return PackageDefinition.builder().name(name).version(version).build();
    }

    private static Package createPackage(PackageDefinition definition, List<Package> dependencies) {
        return Package.builder().definition(definition).dependencies(dependencies).build();
    }

}