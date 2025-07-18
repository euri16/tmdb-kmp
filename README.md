# tmdb-kmp

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