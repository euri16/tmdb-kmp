Please analyze and work on the GitHub issue: $ARGUMENTS.

Follow these steps:

1. Use gh issue view to get the issue details
2. Understand the problem described in the issue
3. Search the codebase for relevant files. Exclude the sandbox apps files.
4. Implement the necessary changes to complete the task
    - Ensure you follow the project's coding standards and architecture
    - Use the provided modular structure (core, data, etc.) appropriately
    - If applicable create a new module
5. Write and run tests to verify the change
6. Ensure code passes linting and type checking
    - Run ./gradlew formatCode and ./gradlew validateCode

- Remember to use the GitHub CLI (`gh`) for all GitHub-related tasks.
- DO NOT create any commits or PRs on this command.