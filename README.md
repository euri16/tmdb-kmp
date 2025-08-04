# tmdb-kmp

A Kotlin Multiplatform library for accessing The Movie Database (TMDB) API. For detailed information about the project architecture, module structure, and design patterns, see [ARCHITECTURE.md](ARCHITECTURE.md).

## Integration Tests

This project includes comprehensive integration tests that verify end-to-end functionality against the real TMDB API. For detailed information about running and understanding these tests, see [INTEGRATION-TESTS.md](INTEGRATION-TESTS.md).

Quick start for integration tests:
```bash
# Set your TMDB API key
export TMDB_API_KEY="your_api_key_here"

# Run all integration tests
./gradlew :core:test:integration-tests:allTest
```

# Git Hooks Setup

This project uses Git hooks to ensure code quality before commits. The hooks are already included in the codebase under `config/hooks/`.

To install the hooks, simply run:
```bash
./gradlew installGitHooks
```

## What does this do?

The pre-commit hook will automatically:
- Check if any Kotlin files are staged for commit
- Run code validation (ktlint, detekt)
- Block the commit if validation fails
- Allow the commit if all checks pass