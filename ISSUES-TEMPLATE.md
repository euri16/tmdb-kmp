# Issue Template Guidelines

## How to Create Effective Issues

When creating issues for this TMDB Multiplatform library project, please follow this template to ensure clear communication and successful implementation.

## Issue Template

### Title
Use a clear, descriptive title that summarizes the feature or bug:
- `feat: Add [specific feature name]`
- `fix: [Brief description of the bug]`
- `refactor: [What is being refactored]`
- `docs: [Documentation update]`

### Description
Provide a clear description of:
- **What** needs to be implemented/fixed
- **Why** this change is needed
- **Context** or background information

### Acceptance Criteria

Define specific, measurable criteria that must be met for the issue to be considered complete. Use the following format:

#### Given/When/Then Format
```
**AC1: [Brief description]**
- **Given** [initial context/state]
- **When** [action or trigger]
- **Then** [expected outcome]

**AC2: [Brief description]**
- **Given** [initial context/state]
- **When** [action or trigger]
- **Then** [expected outcome]
```

#### Checklist Format (Alternative)
```
**Acceptance Criteria:**
- [ ] [Specific requirement 1]
- [ ] [Specific requirement 2]
- [ ] [Specific requirement 3]
- [ ] All existing tests pass
- [ ] New functionality includes appropriate tests
- [ ] Code follows project formatting standards (./gradlew validateCode passes)
```

### Technical Requirements

Include any specific technical considerations:
- **API Endpoints**: Which TMDB API endpoints are involved
- **Modules**: Which project modules need changes (core/, data/, etc.)
- **Platform Considerations**: Android/iOS specific requirements
- **Dependencies**: Any new dependencies needed

### Definition of Done

Standard requirements for all issues:
- [ ] Implementation matches acceptance criteria
- [ ] Unit tests written and passing
- [ ] Integration tests added if applicable
- [ ] Code formatted and passes static analysis (`./gradlew validateCode`)
- [ ] Documentation updated if needed
- [ ] Sandbox apps can demonstrate the feature (if applicable)

### Relevant Links

- Links to the documentation or API references

## Example Issue

### Title
`feat: Add movie external IDs endpoint`

### Description
Implement support for retrieving external IDs (IMDB, Facebook, Instagram, etc.) for movies using the TMDB API `/movie/{movie_id}/external_ids` endpoint.

### Acceptance Criteria

**AC1: Repository method returns external IDs**
- **Given** a valid movie ID
- **When** `getMovieExternalIds(movieId)` is called
- **Then** it returns a `Result<TmdbMovieExternalIds>` with all available external IDs

**AC2: API handles missing external IDs gracefully**
- **Given** a movie with no external IDs
- **When** the API is called
- **Then** it returns an empty external IDs object without error

**AC3: Error handling for invalid movie ID**
- **Given** an invalid movie ID
- **When** `getMovieExternalIds(movieId)` is called
- **Then** it returns a failure result with appropriate error

### Technical Requirements
- **API Endpoint**: `/movie/{movie_id}/external_ids`
- **Modules**: `data/movies`, `core/models`
- **Models**: Create `TmdbMovieExternalIds` data class
- **Testing**: Unit tests for repository and integration tests

### Definition of Done
- [ ] `MoviesRepository.getMovieExternalIds()` method implemented
- [ ] `TmdbMovieExternalIds` model created in `core/models`
- [ ] Unit tests cover success and error scenarios
- [ ] Integration test validates real API call
- [ ] Code passes `./gradlew validateCode`
- [ ] Android sandbox app can display external IDs

## Tips for Writing Good Acceptance Criteria

1. **Be Specific**: Avoid vague terms like "should work well"
2. **Be Testable**: Each criteria should be verifiable
3. **Include Edge Cases**: Consider error scenarios and boundary conditions
4. **Focus on Behavior**: Describe what the system should do, not how
5. **Keep it Simple**: One clear expectation per criteria
6. **Include Non-Functional Requirements**: Performance, security, accessibility when relevant
