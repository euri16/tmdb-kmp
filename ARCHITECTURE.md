# Architecture

This document describes the architecture of the TMDB Kotlin Multiplatform library.

## Overview

The TMDB KMP library is designed as a modular, multiplatform solution for accessing The Movie Database API. It follows clean architecture principles with clear separation of concerns across different layers.

## Module Structure

```
TMDBMultiplatform/
├── core/                    # Foundational library components
│   ├── models/             # Shared data models
│   ├── network/            # HTTP client and networking utilities
│   ├── utils/              # Common utilities
│   └── test/               # Test utilities and common test code
├── data/                   # Data layer modules
│   ├── common/             # Shared data layer utilities
│   └── movies/             # Movies domain implementation
├── androidApp/             # Android sandbox application
├── iosApp/                 # iOS sandbox application
└── mcp-server/             # Model Context Protocol server
```

## Layer Architecture

### Core Layer (`core/`)

**Purpose**: Provides foundational components shared across all modules.

- **`core/models`**: Domain models and data classes
- **`core/network`**: HTTP client configuration using Ktor
- **`core/utils`**: Common utilities and extensions
- **`core/test`**: Shared test utilities and base test classes

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

The library uses a custom `DataResult` type for consistent error handling across all API operations.

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
- Multiplatform targets: Android, iOS, JVM
- Shared source sets for common code
- Platform-specific implementations where needed

## Extension Points

The architecture is designed for extensibility:

1. **New Domains**: Add new `data/` modules following the same pattern
2. **New Platforms**: Add platform-specific implementations
3. **Custom Error Handling**: Extend error handling mechanisms
4. **Additional APIs**: Implement new API endpoints following existing patterns