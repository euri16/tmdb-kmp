# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Kotlin Multiplatform (KMP) library for accessing The Movie Database (TMDB) API. The library provides shared business logic and networking code that can be consumed by Android and iOS applications. The `androidApp/` and `iosApp/` modules serve as sandbox applications for testing and demonstrating the library functionality.

## Architecture

- **Modular Library Architecture**: The project uses a multi-module structure with separation of concerns:
  - `core/` modules contain foundational library components (models, network, utils, test utilities)
  - `data/` modules handle data layer logic (repositories, APIs, DTOs) - the main library functionality
  - `androidApp/` contains Android sandbox app for testing the library
  - `iosApp/` contains iOS sandbox app for testing the library
  - `mcp-server/` provides MCP (Model Context Protocol) server functionality

- **Library Layer Structure**:
  - **Network Layer**: `core/network` - HTTP client setup using Ktor
  - **Data Layer**: `data/movies` - Repository pattern with API abstractions (main library API)
  - **Model Layer**: `core/models` - Shared data models across platforms
  - **Sandbox Apps**: Android/iOS apps for testing and demonstrating library usage

## Development Commands

### Code Quality & Validation
```bash
# Run all code quality checks and formatting verification
./gradlew validateCode

# Check code formatting (Spotless)
./gradlew checkFormatting

# Format code automatically
./gradlew formatCode

# Run static code analysis (Detekt)
./gradlew analyzeCode

# Install git hooks for pre-commit validation
./gradlew installGitHooks
```

### Building & Testing
```bash
# Build the project
./gradlew build

# Run unit tests
./gradlew testAndroidHostTest

# Run integration tests (requires TMDB_API_KEY environment variable)
export TMDB_API_KEY="your_api_key_here"
./gradlew :core:test:integration-tests:allTest

# Run specific integration test class
./gradlew :core:test:integration-tests:jvmTest --tests "*TmdbApiEndToEndTest*"

# Build Android sandbox app
./gradlew :androidApp:build

# Build specific library module
./gradlew :core:network:build
```

## Code Quality Configuration

- **Spotless**: Enforces Kotlin code formatting with ktlint (120 char line limit, 4-space indentation)
- **Detekt**: Static code analysis with custom rules in `config/detekt.yml`
- **Git Hooks**: Pre-commit hooks automatically run validation before commits
- **Compose Rules**: Additional ktlint rules for Jetpack Compose code

## Key Technologies

- **Networking**: Ktor client with content negotiation, logging, and resources
- **Serialization**: kotlinx.serialization for JSON handling
- **Logging**: Kermit for multiplatform logging
- **Testing**: kotlin-test, Mokkery, Turbine for coroutines testing
- **Sandbox UI**: Jetpack Compose for Android, SwiftUI for iOS (testing/demo purposes only)

## API Integration

The library integrates with TMDB API v3. The main HTTP client is configured in `core/network/TmdbHttpClient.kt:19` with bearer token authentication. Repository pattern is used in `data/movies` module for API abstraction.

## Module Dependencies

Library dependencies flow: `core/models` ← `core/network` ← `data/common` ← `data/movies`
Sandbox apps depend on data modules to test and demonstrate library functionality.

## Error Handling Architecture

The library uses a two-tier error handling approach:

- **`ApiResult`** (`core/network/models/ApiResult.kt`) - Low-level network error handling:
  - `HttpError` - HTTP status code errors  
  - `NetworkError` - Network connectivity issues
  - `SerializationError` - JSON parsing failures
  - Uses `getAsApiResult<R, T>()` extension for automatic error mapping

- **`DataResult`** (`data/common/models/DataResult.kt`) - High-level repository error handling:
  - Simple success/failure outcomes for business logic consumers
  - Transforms `ApiResult` to `DataResult` via extension functions

## Integration Tests

Integration tests are located in `core/test/integration-tests/` and test real TMDB API endpoints. Key points:

- **Required**: Set `TMDB_API_KEY` environment variable for tests to run
- **Test behavior**: Tests **fail** (not skip) when API key is missing via `requireApiKey()` function
- **Test utility**: Use `BaseTest.requireApiKey()` for consistent API key validation
- **Coverage**: End-to-end API testing, error handling, cross-platform compatibility

## Important Patterns

### Repository Factory Pattern
```kotlin
val repository = MoviesRepository.factory(apiKey = "your_key")
```

### API Result Extension Usage
```kotlin
// In API implementations
suspend fun getMovie(): ApiResult<MovieDto> = httpClient.getAsApiResult<MovieDto, MovieDto> { 
    // HTTP call
}
```

### DTO to Domain Mapping
DTOs from API are mapped to domain models in `data/movies/models/` to maintain clean separation between API contracts and business logic.

### Multiplatform Testing
- Unit tests use `MainCoroutineRule` for coroutine testing
- Integration tests support all KMP targets (JVM, iOS, Android)
- Mock testing with Mokkery framework for API abstractions