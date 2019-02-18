package com.leszko.npm.npmjsregistry;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.leszko.npm.domain.PackageDefinition;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.google.common.truth.Truth.assertThat;

public class NpmjsRegistryClientTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    private NpmjsRegistryClient client;

    @Before
    public void setIp() {
        String url = String.format("http://localhost:%d", wireMockRule.port());
        client = new NpmjsRegistryClient(new RestTemplateBuilder(), url);
    }

    @Test
    public void provide()
            throws ExecutionException, InterruptedException {
        // given
        String packageName = "accepts";
        String packageVersion = "1.3.5";
        stubFor(get(urlEqualTo(String.format("/%s/%s", packageName, packageVersion)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(npmjsReponse())));

        PackageDefinition input = packageDefinition(packageName, packageVersion);

        // when
        List<PackageDefinition> result = client.provide(input).get();

        // then
        assertThat(result).containsExactly(
                packageDefinition("mime-types", "2.1.18"),
                packageDefinition("negotiator", "0.6.1"));
    }

    @Test(expected = ExecutionException.class)
    public void provideThrowsException()
            throws ExecutionException, InterruptedException {
        // given
        String packageName = "accepts";
        String packageVersion = "1.3.150";
        stubFor(get(urlEqualTo(String.format("/%s/%s", packageName, packageVersion)))
                .willReturn(aResponse().withStatus(404)));

        PackageDefinition input = packageDefinition(packageName, packageVersion);

        // when
        client.provide(input).get();

        // then
        // throws exception
    }

    /**
     * Real response recorded from the online service for the package "accepts:1.3.5".
     */
    private static String npmjsReponse() {
        //language=JSON
        return "{\n"
                + "  \"name\": \"accepts\",\n"
                + "  \"description\": \"Higher-level content negotiation\",\n"
                + "  \"version\": \"1.3.5\",\n"
                + "  \"contributors\": [\n"
                + "    {\n"
                + "      \"name\": \"Douglas Christopher Wilson\",\n"
                + "      \"email\": \"doug@somethingdoug.com\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"name\": \"Jonathan Ong\",\n"
                + "      \"email\": \"me@jongleberry.com\",\n"
                + "      \"url\": \"http://jongleberry.com\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"license\": \"MIT\",\n"
                + "  \"repository\": {\n"
                + "    \"type\": \"git\",\n"
                + "    \"url\": \"git+https://github.com/jshttp/accepts.git\"\n"
                + "  },\n"
                + "  \"dependencies\": {\n"
                + "    \"mime-types\": \"~2.1.18\",\n"
                + "    \"negotiator\": \"0.6.1\"\n"
                + "  },\n"
                + "  \"devDependencies\": {\n"
                + "    \"eslint\": \"4.18.1\",\n"
                + "    \"eslint-config-standard\": \"11.0.0\",\n"
                + "    \"eslint-plugin-import\": \"2.9.0\",\n"
                + "    \"eslint-plugin-markdown\": \"1.0.0-beta.6\",\n"
                + "    \"eslint-plugin-node\": \"6.0.1\",\n"
                + "    \"eslint-plugin-promise\": \"3.6.0\",\n"
                + "    \"eslint-plugin-standard\": \"3.0.1\",\n"
                + "    \"istanbul\": \"0.4.5\",\n"
                + "    \"mocha\": \"~1.21.5\"\n"
                + "  },\n"
                + "  \"files\": [\n"
                + "    \"LICENSE\",\n"
                + "    \"HISTORY.md\",\n"
                + "    \"index.js\"\n"
                + "  ],\n"
                + "  \"engines\": {\n"
                + "    \"node\": \">= 0.6\"\n"
                + "  },\n"
                + "  \"scripts\": {\n"
                + "    \"lint\": \"eslint --plugin markdown --ext js,md .\",\n"
                + "    \"test\": \"mocha --reporter spec --check-leaks --bail test/\",\n"
                + "    \"test-cov\": \"istanbul cover node_modules/mocha/bin/_mocha -- --reporter dot --check-leaks test/\",\n"
                + "    \"test-travis\": \"istanbul cover node_modules/mocha/bin/_mocha --report lcovonly -- --reporter spec --check-leaks test/\"\n"
                + "  },\n"
                + "  \"keywords\": [\n"
                + "    \"content\",\n"
                + "    \"negotiation\",\n"
                + "    \"accept\",\n"
                + "    \"accepts\"\n"
                + "  ],\n"
                + "  \"gitHead\": \"c38d0e968cdc1526f7cc7a718977ee76655c84f5\",\n"
                + "  \"bugs\": {\n"
                + "    \"url\": \"https://github.com/jshttp/accepts/issues\"\n"
                + "  },\n"
                + "  \"homepage\": \"https://github.com/jshttp/accepts#readme\",\n"
                + "  \"_id\": \"accepts@1.3.5\",\n"
                + "  \"_shasum\": \"eb777df6011723a3b14e8a72c0805c8e86746bd2\",\n"
                + "  \"_from\": \".\",\n"
                + "  \"_npmVersion\": \"3.10.10\",\n"
                + "  \"_nodeVersion\": \"6.11.1\",\n"
                + "  \"_npmUser\": {\n"
                + "    \"name\": \"dougwilson\",\n"
                + "    \"email\": \"doug@somethingdoug.com\"\n"
                + "  },\n"
                + "  \"dist\": {\n"
                + "    \"shasum\": \"eb777df6011723a3b14e8a72c0805c8e86746bd2\",\n"
                + "    \"tarball\": \"https://registry.npmjs.org/accepts/-/accepts-1.3.5.tgz\",\n"
                + "    \"fileCount\": 5,\n"
                + "    \"unpackedSize\": 16555\n"
                + "  },\n"
                + "  \"maintainers\": [\n"
                + "    {\n"
                + "      \"name\": \"dougwilson\",\n"
                + "      \"email\": \"doug@somethingdoug.com\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"directories\": {},\n"
                + "  \"_npmOperationalInternal\": {\n"
                + "    \"host\": \"s3://npm-regóistry-packages\",\n"
                + "    \"tmp\": \"tmp/accepts_1.3.5_1519869527663_0.6663620712347182\"\n"
                + "  }\n"
                + "}";
    }

    private static PackageDefinition packageDefinition(String name, String version) {
        return PackageDefinition.builder().name(name).version(version).build();
    }
}