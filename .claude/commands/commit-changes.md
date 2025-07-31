Git Commit the changes to the current branch: $ARGUMENTS.

- Use the GitHub CLI (gh) for all GitHub-related tasks
- DO NOT credit yourself in the commits
- Don't push the changes, just commit them

Follow these steps:

1. **Check status**: Run `git status` to check the current branch
2. **Add files**: Add the files/paths specified in the arguments. If arguments specify "Only the files under X/" or similar, ONLY add files from that directory/path. Default to all files (".") if no specific path arguments are provided.
3. **Commit changes**: Commit the changes with a clear, detailed message
   - Use conventional commit format (feat(<issue_number>):, fix(<issue_number>):, docs(<issue_number>):, etc.). Always include the issue number inside the parentheses.
   - Be concise but descriptive
   - Capitalize first letter
   - Avoid ending with period
4. **Show summary**: Show a brief summary of the changes made

IMPORTANT: Pay careful attention to the arguments - if they specify "Only files under X/" or similar restrictions, respect those limitations and do not add files outside the specified scope.
