Format code and validate code quality: $ARGUMENTS.

Follow these steps:

1. **Format code**: Run `./gradlew formatCode` to automatically format all code
2. **Validate code**: Run `./gradlew validateCode` to check formatting, linting, and code quality
3. **Fix issues**: If validation fails, analyze the output and fix any issues found:
   - Review formatting violations and apply fixes
   - Address Detekt static analysis issues
   - Fix any other code quality violations
4. **Re-run validation**: After fixing issues, run `./gradlew validateCode` again to ensure all problems are resolved
5. **Show summary**: Display a brief summary of what was formatted and validated

**Important Notes**:
- This command ensures code meets project quality standards before commits
- Automatically formats code using Spotless with ktlint rules
- Validates with Detekt static analysis and other quality checks
- Fixes any issues found and re-validates to ensure clean code
