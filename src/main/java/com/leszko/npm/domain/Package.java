package com.leszko.npm.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

/**
 * Domain model class representing an NPM package.
 */
@Builder
@Getter
@EqualsAndHashCode
public final class Package {
    private final PackageDefinition definition;
    private final List<Package> dependencies;
}
