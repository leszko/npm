package com.leszko.npm.npmjsregistry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Respose from the external REST Web Service <a href="npmjs.com>npmjs.com</a>.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
final class NpmjsRegistryResponse {
    private String name;
    private String version;
    private Map<String, String> dependencies = new HashMap<>();
}
