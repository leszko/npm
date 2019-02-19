# NPM

Web Service to resolve NPM dependencies.

## How to run?

1. Clone the repository
2. Run the application

```
./gradlew bootRun
```

## Running tests

To run both unit and acceptance tests use the following command.

```
./gradlew check
```

## Continuous Integration

<https://travis-ci.org/leszko/npm>

[![Build Status](https://travis-ci.org/leszko/npm.svg?branch=master)](https://travis-ci.org/leszko/npm)

## Features Summary

* Retrieving the dependency tree using the external service (npmjs.com)[npmjs.com]
* Caching of the already fetched packages
* Asynchronous execution of the external service REST calls
* Scalability of the web service (together with the shared scalable cache provided by Hazelcast)
* Monitoring and Management provided by the standard Spring Boot Actuator

## Further Improvements

* Better dependency version resolution (to cover all scenarios from [here](<https://docs.npmjs.com/files/package.json#dependencies))
* NPM package cycle detection
* Error handling (to not expose the internal exceptions and return the proper HTTP error codes and messages)
* Retries for the external service calls
* Metrics to the monitoring
* Model for cache entries and the custom serialization (to enable rolling upgrade without loosing cache values)
* Cache persistence