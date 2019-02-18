package com.leszko.npm.npmjsregistry;

import com.leszko.npm.domain.DirectDependenciesProvider;
import com.leszko.npm.domain.PackageDefinition;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Direct dependencies provider which uses the external REST Web Service <a href="npmjs.com>npmjs.com</a>.
 */
public class NpmjsRegistryClient
        implements DirectDependenciesProvider {
    private static final String NPMJS_URL = "https://registry.npmjs.org";

    private final RestTemplate restTemplate;
    private final String npmjsUrl;

    public NpmjsRegistryClient(RestTemplateBuilder restTemplateBuilder) {
        this(restTemplateBuilder, NPMJS_URL);
    }

    public NpmjsRegistryClient(RestTemplateBuilder restTemplateBuilder, String npmjsUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.npmjsUrl = npmjsUrl;
    }

    public CompletableFuture<List<PackageDefinition>> provide(PackageDefinition definition) {
        return CompletableFuture.supplyAsync(() -> provideSync(definition));
    }

    private List<PackageDefinition> provideSync(PackageDefinition definition) {
        String url = String.format("%s/%s/%s", npmjsUrl, definition.getName(), definition.getVersion());
        NpmjsRegistryResponse response = restTemplate.getForObject(url, NpmjsRegistryResponse.class);
        return extractPackageDefinitions(response);
    }

    private List<PackageDefinition> extractPackageDefinitions(NpmjsRegistryResponse response) {
        return response.getDependencies().entrySet().stream()
                       .map(e -> createPackageDefinition(e.getKey(), e.getValue()))
                       .collect(Collectors.toList());
    }

    private PackageDefinition createPackageDefinition(String name, String version) {
        return PackageDefinition.builder()
                                .name(name)
                                .version(toSemanticVersion(version))
                                .build();
    }

    /**
     * Resolves the semantic version from the dependency version string.
     * <p>
     * This implementation is a great simplification and may even give wrong results, for example, ">1.1.0" returns "1.1.0",
     * however the version "1.1.1" or "latest" should be returned.
     * <p>
     * TODO: Improve the implementation to cover the whole "dependencies" syntax.
     *
     * @return semantic version or 'latest' if not found
     * @see <a href="https://docs.npmjs.com/files/package.json#dependencies">dependencies syntax</a>
     */
    private static String toSemanticVersion(String version) {
        Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(version);
        if (matcher.find()) {
            return matcher.group();
        }
        return "latest";
    }
}
