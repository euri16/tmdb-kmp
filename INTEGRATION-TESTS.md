# Integration Tests

This document provides information about the integration tests in this TMDB Kotlin Multiplatform library and instructions on how to run them.

## Overview

Integration tests are located in `core/test/integration-tests/` and provide end-to-end testing of the library's functionality against the real TMDB API. These tests verify:

- Real HTTP client integration with TMDB API
- Authentication and error handling
- Network connectivity and response parsing
- Complete data flow from API to domain models
- Consumer-facing API functionality

## Test Categories

### End-to-End Tests
- **TmdbApiEndToEndTest**: Tests basic API connectivity and authentication
- **NetworkFailureIntegrationTest**: Tests network error scenarios

### Movies API Integration Tests
- **MovieDetailsIntegrationTest**: Movie details retrieval
- **MovieCreditsIntegrationTest**: Movie cast and crew information
- **MovieAlternativeTitlesIntegrationTest**: Alternative movie titles
- **NowPlayingMoviesIntegrationTest**: Currently playing movies
- **PopularMoviesIntegrationTest**: Popular movies listing
- **TopRatedMoviesIntegrationTest**: Top-rated movies
- **UpcomingMoviesIntegrationTest**: Upcoming movies
- **SearchMoviesIntegrationTest**: Movie search functionality
- **UtilityFeaturesIntegrationTest**: Utility features and edge cases
- **ErrorHandlingIntegrationTest**: API error handling scenarios

## Running Integration Tests

### Prerequisites

1. **TMDB API Key**: Get a free API key from [TMDB API Settings](https://www.themoviedb.org/settings/api)
2. **Environment Variable**: Set your API key as an environment variable:
   ```bash
   export TMDB_API_KEY="your_api_key_here"
   ```

### Running All Integration Tests

```bash
./gradlew :core:test:integration-tests:allTest
```

### Running Specific Platform Tests

```bash
# JVM tests only
./gradlew :core:test:integration-tests:jvmTest

# iOS tests only
./gradlew :core:test:integration-tests:iosTest
```

### Running Specific Test Classes

```bash
# Run end-to-end tests
./gradlew :core:test:integration-tests:jvmTest --tests "*TmdbApiEndToEndTest*"

# Run movie details tests
./gradlew :core:test:integration-tests:jvmTest --tests "*MovieDetailsIntegrationTest*"

# Run error handling tests
./gradlew :core:test:integration-tests:jvmTest --tests "*ErrorHandlingIntegrationTest*"
```

## Test Behavior

### With Valid API Key
When `TMDB_API_KEY` environment variable is set with a valid key:
- Tests make real HTTP requests to TMDB API
- Responses are validated against actual data
- Network errors and API changes are detected
- Full end-to-end functionality is verified

### Without API Key
When `TMDB_API_KEY` is not set or invalid:
- Tests skip gracefully with informative messages
- No network requests are made
- Clear instructions are provided for running with real API

## Test Output

Integration tests provide detailed output including:
- Skip reasons when API key is missing
- Success confirmations with actual data samples
- Error details for debugging authentication issues
- Performance metrics and response validation

## Important Notes

1. **Rate Limits**: TMDB API has rate limits. Running tests frequently may trigger rate limiting.
2. **Network Dependency**: These tests require internet connectivity.
3. **API Changes**: Tests may fail if TMDB changes their API structure.
4. **Real Data**: Tests work with real movie data which may change over time.
5. **Authentication**: Invalid API keys will cause authentication failures by design.

## Troubleshooting

### Common Issues

**Test Skipped Messages**: Ensure `TMDB_API_KEY` environment variable is set
```bash
export TMDB_API_KEY="your_actual_api_key"
./gradlew :core:test:integration-tests:allTest
```

**401 Unauthorized Errors**: Verify your API key is valid and active

**Network Timeouts**: Check internet connectivity and TMDB API status

**Gradle Task Not Found**: Ensure you're running from project root directory

### Getting Help

If integration tests consistently fail:
1. Verify API key validity at [TMDB API Settings](https://www.themoviedb.org/settings/api)
2. Check TMDB API status and documentation
3. Review test output for specific error messages
4. Ensure all dependencies are properly resolved with `./gradlew build`
