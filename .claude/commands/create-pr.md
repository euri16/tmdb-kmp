Create a PR against this branch: $ARGUMENTS.

Follow these steps:

1. ALWAYS start by running git status to check what branch you're on
2. Use this template: `gh pr create --base develop --head <branch_name> --title "clear, detailed
description of changes" --body "clear, detailed description of changes"`
3. Always create Pull Requests against the branch the user specifies (or default to develop branch).
NEVER EVER AGAINST main
4. If you run into issues, STOP and explain the error to the user.

Remember:

- Use the GitHub CLI (gh) for all GitHub-related tasks
- DO NOT credit yourself

**PR Title Guidelines**:
   - Use conventional commit format (feat(<issue_number>):, fix(<issue_number>):, docs(<issue_number>):, etc.)
   - Be concise but descriptive
   - Capitalize first letter
   - Avoid ending with period

**PR Description Structure**:
   - Brief summary of changes
   - List of key modifications
   - Testing notes if applicable
   - Any breaking changes or migration notes
   - Link to related issues if mentioned

**Error Recovery**: If gh CLI fails:
   - Provide clear error explanation
   - Suggest common solutions (authentication, permissions, etc.)
   - Offer alternative approaches if needed

**Confirmation**: After successful PR creation, provide:
   - PR URL
   - PR number
   - Brief summary of what was created