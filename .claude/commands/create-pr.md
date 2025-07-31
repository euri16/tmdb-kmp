Create a PR against this branch: $ARGUMENTS.

Pay special attention to the steps in `Follow these steps` and its order. Use the `Remember` section as a guide to take in consideration while working in every step.

Follow these steps:

1. **Check current status**: Run `git status` to check what branch you're on
2. **Get branch name**: Get the current branch name with `git branch --show-current`
3. **Determine target branch**: 
   - If arguments specify a target branch, use that
   - Otherwise, use the develop branch
4. **Push current branch**: Push the current branch to the remote repository
5. **Create PR**: Use this template: `gh pr create --base <target_branch> --head <current_branch> --title "clear, detailed description of changes" --body "clear, detailed description of changes"`
6. **Update issue status**: When the PR is successfully created, add a comment to the issue summarizing the work completed
7. **Validate target**: Always create Pull Requests against the branch the user specifies or the project default.
   NEVER EVER AGAINST main unless explicitly specified
8. **Handle errors**: If you run into issues, STOP and explain the error to the user

Remember:

**PR Title Guidelines**:
   - Use conventional commit format (feat(<issue_number>):, fix(<issue_number>):, docs(<issue_number>):, etc.). Always include the issue number inside the parentheses.
   - Be concise but descriptive
   - Capitalize first letter
   - Avoid ending with period

**PR Description Structure**:
   - Use these sections in this order:
     - **Related Issues**:
        - Hyperlink to any related issues or tickets. For example: Resolves #123
     - **Summary**: Brief overview of what the PR does
     - **Changes**: Detailed list of changes made. Use bullet points.
     - **Testing**: Optional. Instructions on how to test the changes. Only add this section
                    when the PR needs special instructions to test.

**Error Recovery**: If gh CLI fails:
   - Provide clear error explanation
   - Suggest common solutions (authentication, permissions, etc.)
   - Offer alternative approaches if needed

**Confirmation**: After successful PR creation, provide:
   - PR URL
   - PR number
   - Brief summary of what was created

**General Guidelines**:
   - Use the GitHub CLI (gh) for all GitHub-related tasks
   - DO NOT credit yourself in the commits
