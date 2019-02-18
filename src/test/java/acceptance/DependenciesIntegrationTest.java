package acceptance;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.leszko.npm.Application;
import com.leszko.npm.domain.DirectDependenciesProvider;
import com.leszko.npm.npmjsregistry.NpmjsRegistryClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class, DependenciesIntegrationTest.class})
@WebAppConfiguration
@Configuration
public class DependenciesIntegrationTest {
    // TODO: Change to dynamic port
    private static final int WIREMOCK_PORT = 5555;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WIREMOCK_PORT);

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void dependencies()
            throws Exception {
        // given
        String parentPackage = "parent";
        String parentVersion = "1.0.0";
        stub(parentPackage, parentVersion, parentResponse());
        stub("child", "1.2.3", childResponse());
        stub("grandchild", "4.5.6", grandchildResponse());

        // when & then
        this.mockMvc
                .perform(get("/{name}/{version}", parentPackage, parentVersion))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().json(expectedResult()));

    }

    private static void stub(String packageName, String packageVersion, String response) {
        stubFor(WireMock.get(urlEqualTo(String.format("/%s/%s", packageName, packageVersion)))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withStatus(200)
                                .withBody(response)));
    }

    private static String parentResponse() {
        //language=JSON
        return "{\n"
                + "  \"name\": \"parent\",\n"
                + "  \"version\": \"1.0.0\",\n"
                + "  \"dependencies\": {\n"
                + "    \"child\": \"1.2.3\"\n"
                + "  }\n"
                + "}";
    }

    private static String childResponse() {
        //language=JSON
        return "{\n"
                + "  \"name\": \"child\",\n"
                + "  \"version\": \"1.2.3\",\n"
                + "  \"dependencies\": {\n"
                + "    \"grandchild\": \"4.5.6\"\n"
                + "  }\n"
                + "}";
    }

    private static String grandchildResponse() {
        //language=JSON
        return "{\n"
                + "  \"name\": \"grandchild\",\n"
                + "  \"version\": \"4.5.6\",\n"
                + "  \"dependencies\": {}"
                + "}";
    }

    private static String expectedResult() {
        //language=JSON
        return "{\n"
                + "  \"name\": \"parent\",\n"
                + "  \"version\": \"1.0.0\",\n"
                + "  \"dependencies\": [\n"
                + "    {\n"
                + "      \"name\": \"child\",\n"
                + "      \"version\": \"1.2.3\",\n"
                + "      \"dependencies\": [\n"
                + "        {\n"
                + "          \"name\": \"grandchild\",\n"
                + "          \"version\": \"4.5.6\"\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  ]\n"
                + "}";
    }

    @Bean
    DirectDependenciesProvider dependenciesProvider() {
        String url = String.format("http://localhost:%d", WIREMOCK_PORT);
        return new NpmjsRegistryClient(new RestTemplateBuilder(), url);
    }

}
