package com.leszko.npm.api;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
final class DependenciesResponse {
    private final String name;
    private final String version;
    private final List<DependenciesResponse> dependencies;
}
