package com.leszko.npm.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Domain model representing a definition of an NPM package.
 */
@Builder
@Getter
@EqualsAndHashCode
public final class PackageDefinition {
    private final String name;
    private final String version;
}
