package com.leszko.npm.cache;

import com.leszko.npm.domain.DirectDependenciesProvider;
import com.leszko.npm.domain.PackageDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CachingDirectDependenciesProviderTest {
    private static final PackageDefinition INPUT = PackageDefinition.builder().name("inputName").version("version").build();
    private static final List<PackageDefinition> OUTPUT = Arrays
            .asList(PackageDefinition.builder().name("outputName").version("outputVersion").build());
    @Mock
    private DirectDependenciesProvider internalProvider;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    private CachingDirectDependenciesProvider cachingProvider;

    @Before
    public void setUp() {
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        cachingProvider = new CachingDirectDependenciesProvider(internalProvider, cacheManager);
    }

    @Test
    public void provideFoundInCache()
            throws ExecutionException, InterruptedException {
        // given
        given(cache.containsKey(INPUT)).willReturn(true);
        given(cache.get(INPUT)).willReturn(OUTPUT);

        // when
        List<PackageDefinition> result = cachingProvider.provide(INPUT).get();

        // then
        assertThat(result).isEqualTo(OUTPUT);
    }

    @Test
    public void provideNotFoundInCache()
            throws ExecutionException, InterruptedException {
        // given
        given(internalProvider.provide(INPUT)).willReturn(completedFuture(OUTPUT));

        // when
        List<PackageDefinition> result = cachingProvider.provide(INPUT).get();

        // then
        assertThat(result).isEqualTo(OUTPUT);
        verify(cache).put(INPUT, OUTPUT);
    }

    @Test
    public void provideLatestNotCached()
            throws ExecutionException, InterruptedException {
        // given
        PackageDefinition input = PackageDefinition.builder().name("input").version("latest").build();
        given(internalProvider.provide(input)).willReturn(completedFuture(OUTPUT));

        // when
        List<PackageDefinition> result = cachingProvider.provide(input).get();

        // then
        assertThat(result).isEqualTo(OUTPUT);
        verifyNoMoreInteractions(cache);
    }
}