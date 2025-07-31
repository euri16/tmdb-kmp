Please analyze and work on the GitHub issue: $ARGUMENTS.

Follow these steps:

1. **Validate and parse issue**: Extract issue number from arguments (supports #123, issue URL, or just 123)
2. **Get issue details**: Use `gh issue view <issue_number>` to get the issue details
3. **Assign issue**: Assign the issue to yourself with `gh issue edit <issue_number> --add-assignee @me`
4. **Create working branch**: Create and checkout a new branch named `task/tmdb-kmp-<issue_number>`
5. **Understand the problem**: Analyze the issue description, requirements, and acceptance criteria
6. **Search the codebase**: Find relevant files and understand current implementation. Exclude the sandbox apps files from main implementation.
7. **Implement the necessary changes**:
    - Ensure you follow the project's coding standards and architecture
    - Use the provided modular structure (core, data, etc.) appropriately
    - If applicable create a new module
    - Don't do any commit 
8. **Write and run tests**: 
    - Create or update tests for your changes
    - Run tests with `./gradlew allTests`
    - Ensure all tests pass
9. **Validate code quality**:
    - Run `./gradlew formatCode` to format code
    - Run `./gradlew validateCode` to check linting and validation
    - Fix any issues found

**Important Notes**:
- Remember to use the GitHub CLI (`gh`) for all GitHub-related tasks
- DON'T do any commit during implementation
- DO NOT create PRs automatically - that's a separate command
- If the issue is unclear or missing information, ask clarifying questions in a comment