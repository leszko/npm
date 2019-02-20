# NPM Analyzer

Web Service to resolve NPM dependencies.

## Quick Start

```
./gradlew bootRun
```

## Running tests

To run both unit and acceptance tests use the following command:

```
./gradlew check
```

## Continuous Integration

<https://travis-ci.org/leszko/npm>

[![Build Status](https://travis-ci.org/leszko/npm.svg?branch=master)](https://travis-ci.org/leszko/npm)

## Features Summary

* Retrieving the dependency tree using an external service ([npmjs.com](npmjs.com))
* Caching of external service calls
* Asynchronous execution external service calls
* Scalability (together with the distributed caching layer provided by Hazelcast)
* Monitoring and management provided by the standard Spring Boot Actuator

## Further Improvements

* Better dependency version resolution (to cover all scenarios from [here](https://docs.npmjs.com/files/package.json#dependencies))
* Cycle dependencies detection
* Error handling (to not expose internal exceptions and return HTTP error codes and messages)
* Retries for external service calls
* Metrics for better monitoring
* Dedicated model for the caching layer (to enable rolling upgrade without loosing cache values)
* Cache persistence