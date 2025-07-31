Create a PR against this branch: $ARGUMENTS.

Follow these steps:

1. ALWAYS start by running git status to check what branch you're on
2. Use this template: `gh pr create --base develop --head <current_branch_name> --title "clear, detailed
description of changes" --body "clear, detailed description of changes"`
3. Always create Pull Requests against the branch the user specifies (or default to develop branch).
NEVER EVER AGAINST main
4. If you run into issues, STOP and explain the error to the user.

Remember:

- Use the GitHub CLI (gh) for all GitHub-related tasks
- DO NOT credit yourself in the commits

**PR Title Guidelines**:
   - Use conventional commit format (feat(<issue_number>):, fix(<issue_number>):, docs(<issue_number>):, etc.). Always include the issue number inside the parentheses.
   - Be concise but descriptive
   - Capitalize first letter
   - Avoid ending with period

**PR Description Structure**:
   - Use these sections in this order:
     - **Related Issues**: Link to any related issues or tickets
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