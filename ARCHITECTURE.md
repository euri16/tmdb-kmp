# Architecture

This document describes the architecture of the TMDB Kotlin Multiplatform library.

## Overview

The TMDB KMP library is designed as a modular, multiplatform solution for accessing The Movie Database API. It follows clean architecture principles with clear separation of concerns across different layers.

## Module Structure

```
TMDBMultiplatform/
├── core/                    # Foundational library components
│   ├── models/             # Shared data models and domain objects
│   ├── network/            # HTTP client configuration and extensions
│   │   ├── extensions/     # Ktor extensions (getAsApiResult, error handling)
│   │   └── models/         # ApiResult and network-specific models
│   ├── utils/              # Common utilities and platform abstractions
│   │   └── DispatcherProvider # Coroutine dispatcher dependency injection
│   └── test/               # Comprehensive test infrastructure
│       ├── test-common/    # Shared test utilities (BaseTest, MainCoroutineRule)
│       └── integration-tests/ # Real API integration test framework
├── data/                   # Data layer modules
│   ├── common/             # Shared data layer utilities and base classes
│   │   ├── extensions/     # ApiResult.toDataResult() transformations
│   │   └── models/         # DataResult and common data types
│   └── movies/             # Movies domain implementation
│       ├── api/            # API client abstractions and implementations
│       ├── repository/     # Repository interfaces and implementations
│       └── models/         # DTOs and domain model mappers
├── androidApp/             # Android sandbox application (Jetpack Compose)
├── iosApp/                 # iOS sandbox application (SwiftUI)
├── mcp-server/             # Model Context Protocol server
│   └── tools/              # MCP tools for movie operations
├── config/                 # Code quality configuration
│   └── detekt.yml          # Static analysis rules
└── gradle/                 # Build system configuration
    └── libs.versions.toml  # Centralized dependency management
```

## Layer Architecture

### Core Layer (`core/`)

**Purpose**: Provides foundational components shared across all modules.

- **`core/models`**: Domain models and data classes
- **`core/network`**: HTTP client configuration using Ktor with critical extensions:
  - `getAsApiResult<R, T>()` - Handles HTTP errors and maps to `ApiResult`
  - Comprehensive error mapping from HTTP status codes
  - Proper coroutine cancellation handling
- **`core/utils`**: Platform abstractions and common utilities:
  - `DispatcherProvider` - Dependency injection for coroutine dispatchers
  - Platform-specific implementations for environment variables
  - Date parsing extensions (`String.localDateOrNull()`)
- **`core/test`**: Comprehensive test infrastructure:
  - `BaseTest` interface and `MainCoroutineRule` for coroutine testing
  - `NetworkTestFactory` for MockEngine configuration
  - Integration test framework with real API endpoints

### Data Layer (`data/`)

**Purpose**: Implements the repository pattern and handles API communication.

- **`data/common`**: Shared data layer utilities and base classes
- **`data/movies`**: Movies domain implementation including:
  - Repository interfaces and implementations
  - API client abstractions
  - DTOs and mappers
  - Network resource handling

### Application Layer

**Purpose**: Sandbox applications for testing and demonstrating library functionality.

- **`androidApp/`**: Android application using Jetpack Compose
- **`iosApp/`**: iOS application using SwiftUI

## Dependency Flow

```
core/models ← core/network ← data/common ← data/movies
                                              ↑
                                    androidApp/iosApp
```

Library modules follow a strict dependency hierarchy:
1. `core/models` - No dependencies on other library modules
2. `core/network` - Depends on `core/models`
3. `data/common` - Depends on `core/network` and `core/models`
4. `data/movies` - Depends on `data/common` and core modules
5. Sandbox apps depend on data modules

## Key Architectural Patterns

### Repository Pattern

The library uses the repository pattern to abstract API access:

```kotlin
interface MoviesRepository {
    suspend fun getPopularMovies(): DataResult<List<TmdbMovie>>
    // Other movie operations...
}

class MoviesRepositoryImpl(
    private val api: MoviesApi
) : MoviesRepository {
    // Implementation...
}
```

### API Abstraction

Network calls are abstracted through API interfaces:

```kotlin
interface MoviesApi {
    suspend fun getPopularMovies(): MovieListResponse
}

class MoviesApiImpl(
    private val httpClient: HttpClient
) : MoviesApi {
    // Ktor-based implementation...
}
```

### Error Handling

The library uses a two-tier error handling approach:

- **`ApiResult`** (`core/network/models/ApiResult.kt`) - Low-level network error handling with specific error types:
  - `HttpError` - HTTP status code errors
  - `NetworkError` - Network connectivity issues  
  - `SerializationError` - JSON parsing failures

- **`DataResult`** (`data/common/models/DataResult.kt`) - High-level repository error handling that abstracts network details into simple success/failure outcomes for business logic consumers.

### DTO Mapping

Data Transfer Objects (DTOs) are mapped to domain models to maintain clean separation between API contracts and business logic.

## Testing Strategy

### Unit Tests
- Repository implementations tested with mocked APIs
- Mapper functions tested for data transformation
- Utility functions tested in isolation

### Integration Tests
- End-to-end API testing with real TMDB endpoints
- Cross-platform compatibility testing
- Error handling and edge case validation

### Sandbox Testing
- Manual testing through Android and iOS applications
- UI integration testing
- Performance validation

## Technology Stack

### Networking
- **Ktor Client**: Multiplatform HTTP client
- **kotlinx.serialization**: JSON serialization/deserialization
- **Content Negotiation**: Automatic JSON handling

### Logging
- **Kermit**: Multiplatform logging solution

### Testing
- **kotlin-test**: Multiplatform testing framework
- **Mokkery**: Mocking framework for Kotlin Multiplatform
- **Turbine**: Testing utilities for coroutines and flows

### Code Quality
- **Spotless**: Code formatting with ktlint
- **Detekt**: Static code analysis
- **Git Hooks**: Pre-commit validation

## Configuration

### HTTP Client
- Bearer token authentication for TMDB API
- Request/response logging for debugging
- Error handling and retry logic
- Base URL configuration

### Build Configuration
- **Multiplatform targets**: Android, iOS (iosX64, iosArm64, iosSimulatorArm64), JVM
- **Shared source sets**: Common code with platform-specific implementations
- **Version catalog**: Centralized dependency management in `gradle/libs.versions.toml`
- **Project accessors**: TYPESAFE_PROJECT_ACCESSORS for type-safe module dependencies (`projects.core.network`)
- **Custom Gradle tasks**:
  - `validateCode` - Runs all code quality checks
  - `analyzeCode` - Detekt static analysis
  - `checkFormatting` - Spotless formatting verification  
  - `formatCode` - Automatic code formatting
  - `installGitHooks` - Sets up pre-commit validation hooks
- **iOS framework naming**: Custom `xcfName` conventions for different modules
- **Android library configuration**: Namespace conventions, SDK targets, test runners

## Extension Points

The architecture is designed for extensibility:

1. **New Domains**: Add new `data/` modules following the same pattern
2. **New Platforms**: Add platform-specific implementations
3. **Custom Error Handling**: Extend error handling mechanisms
4. **Additional APIs**: Implement new API endpoints following existing patterns