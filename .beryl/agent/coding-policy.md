# Coding Policy

This policy is the implementation agent's working contract for the CS2103/T team project. It translates the tP grading rubric, course constraints, and repository conventions into rules that can be checked during every change.

The policy applies to production code, tests, UI resources, persistence, documentation, diagrams, build configuration, and Git history. Repository instructions, an explicit user request, and a ratified design decision take precedence when they conflict with a generic rule here. When a conflict is material, stop and report it instead of silently choosing a weaker standard.

Use `MUST` for a release-blocking rule, `SHOULD` for a strong default that may be overridden with a documented reason, and `MAY` for an optional technique.

## Rubric-Aligned Non-Negotiables

Every change must be reviewed against the five assessed areas below. The agent must not optimize for line count or feature count at the expense of product fit, correctness, testability, or clarity.

### Product design

* A feature MUST solve a stated problem for the intended TutorTrack user and MUST fit the existing product rather than being an isolated novelty.
* Before implementation, the change MUST identify the user goal, the normal path, the important invalid or boundary paths, and how the user can recover from failure.
* A feature is incomplete if the happy path works but legitimate user input, incorrect commands, extra parameters, empty data, duplicate data, missing data, or persistence failure produces an undefined, misleading, or unusable result.
* Features MUST remain practical for a fast typist. Prefer clear commands, predictable keyboard focus, useful shortcuts where the existing UI supports them, concise feedback, and workflows that do not require unnecessary mouse navigation.
* The agent MUST reject scope that does not fit the target user, product vocabulary, non-goals, or course constraints. Record a durable scope or architecture decision in `design-tree.md` or an ADR when needed.
* A feature that is difficult to test is a design defect. Expose a small, deterministic public boundary and make observable outcomes explicit.

### Implementation and code quality

* New or changed code MUST follow the Java, object-oriented design, naming, layout, documentation, error-handling, logging, assertion, and defensive coding rules below.
* Production code MUST show appropriate evidence of logging, exception handling, assertions, and defensive coding over the product as a whole. Do not add meaningless examples merely to satisfy a checklist; use each technique where its semantics require it.
* Methods MUST remain at one level of abstraction, have one clear purpose, and be short enough to understand without mentally simulating a large block. Treat more than roughly 30 lines as a prompt to extract a named helper, not as an automatic violation.
* Code MUST avoid noticeable copy-paste duplication, including duplicated test setup and assertions. Extract shared behavior only when the abstraction is clearer than the duplication; do not create speculative frameworks.
* Code MUST be readable by another team member who did not write it. Prefer explicit, simple, domain-named code over cleverness, implicit behavior, or premature optimization.
* Every implementation change MUST preserve unrelated existing behavior. A broad rewrite is not justified by formatting convenience.

### Quality assurance

* Every behavior change MUST have automated tests at the narrowest useful boundary and at least one relevant edge-case test.
* Tests MUST cover the behavior promised by the User Guide, including malformed input and extra parameters where the interface accepts commands.
* A test suite MUST verify observable results, not merely that a method was called or that an internal implementation detail currently exists.
* The agent MUST manually reason through acceptance/system-test paths and add or update the Developer Guide's manual-testing instructions for any new user-testable feature.
* No test may be weakened, deleted, skipped, made less precise, or changed to accept incorrect behavior merely to make the implementation pass.
* A release candidate MUST be runnable from the packaged artifact on Java 25, and the documented launch and test path MUST match the actual artifact.

### Acceptance and peer-testing feedback

When a peer, tutor, or acceptance test reports a possible bug or feature flaw:

* Reproduce it using the reported steps, the stated environment, and a clean data file where appropriate. If reproduction fails, make a reasonable second attempt using the visible symptoms, screenshots, logs, and likely preconditions before declaring it unreproducible.
* Treat a behavior that differs from the User Guide, a legitimate user action that is not handled, an incorrect-command or extra-parameter failure, a misleading error, or an incomplete feature as a real defect candidate.
* Classify the report as a functionality bug, documentation bug, or feature flaw based on what is actually wrong. Do not choose a label to improve a grade, reject a valid report on a technicality, or downgrade a report without evidence.
* Fix accepted functionality bugs with a regression test. Fix documentation bugs in the affected source document and verify links, examples, and generated output. Discuss feature flaws in the relevant design artifact before implementing a change.
* Preserve the reporter's original reproduction steps and evidence. Do not rewrite, delete, or obscure a report to make the product appear healthier.
* Test platform-specific reports on the affected operating system when possible. A bug limited to one supported platform is still a valid bug.

### Input quality and defect triage

* Distinguish deliberate sabotage from foreseeable mistakes. Extreme input need not be supported when it can arise only through intentional abuse, but an accidental malformed command, omitted separator, numeric overflow, or other plausible mistake MUST NOT crash the application, corrupt data, or leave it unusable.
* Input limits MAY protect usability and safety only when reasonable for the target user and real-world data. Treat unusually long values as a UI-resilience concern: prevent layout breakage and important information from being hidden or truncated. Classify a merely cosmetic effect as very low severity, but raise severity when it hinders the user.
* Do not reject symbols or otherwise valid-looking user data merely because they are inconvenient to parse unless the restriction has a documented operational justification. A restriction that prevents users from recording expected real-world data is a feature flaw; prefer a parser or representation that accepts it when practical.
* Prefer warnings over blocking for non-harmful deviations from an expected format, including multiple contact details in a free-text field or past dates kept for records. Conversely, block or clearly warn about input that could harm operation, data integrity, or user understanding.
* User-facing validation errors MUST identify the specific failed rule and offer recovery. Distinguish an invalid value from an incorrect format when doing so is practical; an inclusive message such as "invalid date or incorrect format" is acceptable when the distinction adds no useful value.
* Command formats MUST optimize for accurate, fast typing: avoid needless case sensitivity, long keywords, and hard-to-type delimiters. Where it improves recall and speed without ambiguity, support equivalent short and long forms. Match case sensitivity to the represented real-world entity; names and ordinary search terms SHOULD be case-insensitive.
* Judge feature usefulness, not only technical correctness. Searches SHOULD favor case-insensitive matching and OR-style multi-keyword retrieval when that helps users recover results despite imperfect recall; a stricter behavior needs a product rationale.
* Keep terminal output presentable and non-alarming to a user who sees it. Terminal-only issues can be lower severity or out of scope, but misleading or alarming output remains a defect candidate.
* When classifying a mismatch, use `FunctionalityBug` or `FeatureFlaw` if product behavior must change, and `DocumentationBug` only if the User Guide must change. Do not relabel a product defect as documentation merely to avoid a behavior fix.

### Documentation

* A change that affects user behavior MUST update `docs/UserGuide.md` in the same feature boundary.
* A change that affects architecture, implementation flow, data shape, extension points, testing, or setup MUST update `docs/DeveloperGuide.md`, the relevant diagram, or the relevant project-owned design file.
* User Guide and Developer Guide content MUST describe the latest product, not an aspirational or stale version. Examples, commands, screenshots, diagrams, links, error messages, and terminology MUST be verified against the implementation.
* Documentation MUST be concise enough to scan, detailed enough to operate or maintain the product, and free from unnecessary repetition, typos, broken links, unexplained project jargon, and contradictory instructions.
* If the repository has a product website, website User Guide or Developer Guide pages, or a Project Portfolio Page, a user-visible or architectural change MUST update the affected page or clearly record why it is out of scope. The website, UG, DG, and portfolio MUST not describe different versions of the product.
* A product demo, release note, or showcase script MUST use a real current workflow and MUST not claim behavior that is not present in the packaged product.

### Project management and contribution evidence

* Changes MUST be delivered as small, working, breadth-first increments. Do not accumulate unrelated implementation, tests, and documentation for a big-bang merge.
* Each feature change SHOULD be traceable to a focused issue or task, a focused pull request, review evidence, tests, and (when applicable) a release or milestone.
* Commits MUST be logical and attributable. Do not mix unrelated features, generated output, opportunistic cleanup, or policy changes in one commit.
* The agent MUST not claim that code volume equals effort. The course's 500--600 functional-lines-per-person figure is only a rough secondary estimate; quality, value, and coherent contribution matter more.
* Human team members remain responsible for equal team-task participation, assigned roles, peer review, milestone and release upkeep, deadline management, the required commits across project weeks, and peer evaluation. The agent must surface these follow-ups when a change affects them but must not fabricate that evidence.

## Product And Course Constraints

Unless a later course instruction or ratified team decision explicitly changes one of these constraints, the agent MUST enforce all of them:

* **Brownfield:** evolve the supplied codebase. Major changes are allowed only as small steps where each step leaves a working product.
* **Typing preferred:** design for users who type quickly and prefer typing to other input methods.
* **Single user:** do not introduce multi-user accounts, shared-user data, concurrent remote access, or a shared data file used by different users.
* **Incremental delivery:** implement breadth-first vertical slices with a reasonably consistent delivery rate; do not postpone the whole product to one late burst.
* **Local editable persistence:** store data locally in a human-editable text file and retain at least the existing AddressBook-level editing support: correctly edited data must load, while a wrongly edited file may lose data only to the extent documented by the baseline User Guide. Do not reduce this support, promise more in the User Guide without delivering it, or use a format unsuitable for manual editing. Serialization details belong behind the persistence boundary.
* **Data sandbox:** read and write only within the folder containing the JAR and its subfolders; do not change operating-system settings.
* **No DBMS:** do not use a database management system for product data.
* **Object orientation:** use object-oriented design as the primary style; mix in other styles only where there is a clear, documented benefit.
* **Platform independence:** support Windows, Linux, and macOS. Avoid operating-system-specific libraries, paths, shell commands, and behavior.
* **Java 25:** the application MUST run with Java 25 as the only installed Java version required by the user.
* **Portable distribution:** the product MUST run without an installer.
* **No team-owned remote server:** the product MUST not depend on a server operated by the team.
* **External software:** a new third-party library or service MUST be free or open source with permissive licensing, require no user installation, obey every other constraint, and receive teaching-team approval before use.
* **Screen sizes:** the GUI MUST work well at 1920x1080 or higher at 100% and 125% scaling, and remain usable at 1280x720 or higher at 150% scaling.
* **Single JAR:** the final product MUST be packaged as one executable JAR.
* **File-size and release constraints:** before a release, re-check the current course constraint page for any stated artifact-size limit and verify the packaged artifact against it.

Do not weaken the brownfield baseline casually. Existing AddressBook behavior, tests, documentation, and GitHub Actions remain authoritative until a ratified TutorTrack slice deliberately changes them.

## Security And Tooling

* Prefer read-only repository and integration access until the task requires a scoped write.
* Do not make material changes to `build.gradle`; the established build configuration is part of the project baseline. A narrowly scoped change requires explicit user approval.
* Never place credentials, tokens, realistic personal data, or sensitive lesson content in source, tests, documentation, prompts, screenshots, or logs.
* Use deterministic local checks before mutable remote operations. Treat an explicitly preserved root contract or hook as external ownership, not as Beryl enforcement.
* Confirm the exact target before destructive operations, migrations, dependency upgrades, or writes outside the repository workspace.

## Bounded Context And Naming Rules

New TutorTrack code MUST use the vocabulary in `.beryl/agent/ubiquitous-language.md`:

* `TutorTrack` is the product, not `AddressBook`.
* `Tutor` manages a `StudentRoster`.
* A `Student` has a `ParentGuardianContact`, `Subject`, `CurrentLevel`, and `SessionHistory` of `SessionNote` objects.
* `StudentProfile` is the composed profile view.
* `Storage` is the local persistence mechanism.

Do not use `person`, `contact`, `remark`, `client`, `account`, or `activity` for new TutorTrack concepts when one of the terms above is intended. Existing AddressBook names may remain in untouched legacy code, but new code must not silently mix the two vocabularies.

Respect these boundaries:

* Student-roster code owns student records and roster invariants, not UI controls, file formats, or session-note rendering.
* Profile/session code owns one student's profile composition and chronological notes, not cross-student queries or file I/O.
* Persistence code owns serialization, loading, saving, migration, and file failures, not domain validation or JavaFX state.
* JavaFX code owns presentation and input collection, not domain rules or serialization.
* A context may use another context only through a small public API. Never import or mutate another context's internal representation.
* Public APIs MUST not expose JavaFX controls, Jackson/serialization DTOs, mutable collections, or file paths outside their owning boundary.
* Domain objects MUST enforce their invariants at construction and mutation; controllers and serializers must not be the only protection.

## Java Coding Standard

Java follows the SE-EDU Java coding standard. Checkstyle is authoritative for mechanically checkable rules; manual review is authoritative for intent and quality rules that Checkstyle cannot detect.

### Naming

* Package names MUST be lowercase English words. New TutorTrack classes stay under the existing `seedu.address` hierarchy until a ratified architecture decision changes it.
* Class and enum names MUST be PascalCase nouns: `Student`, `SessionNote`.
* Method names MUST be camelCase verbs: `addStudent`, `findSessionNotes`.
* Variable and parameter names MUST be camelCase and scoped no wider than necessary.
* Constants MUST be `SCREAMING_SNAKE_CASE` and grouped with related constants.
* Boolean variables and methods MUST read as predicates: `isVisible`, `hasSessionNotes`, `canSave`, `shouldRetry`, `wasLoaded`.
* Boolean setters MUST use the form `setFound(boolean isFound)`.
* Collection names MUST be plural: `students`, `sessionNotes`, `values`.
* Test methods MAY use `featureUnderTest_testScenario_expectedBehavior`.
* Names and comments MUST use English and American spelling. Acronyms inside names are written like ordinary words: `parseJson`, `exportHtml`, `openDvdPlayer`, not `parseJSON`, `exportHTML`, or `openDVDPlayer`.
* Long-lived or wide-scope variables MUST have descriptive names. Short names such as `i`, `j`, and `k` are reserved for small loop scopes, with `j` and `k` used only for nested loops.

### Layout and statements

* Use four spaces for indentation, never tabs in Java source.
* Keep lines below the 110-character soft limit and never exceed the 120-character hard limit.
* Wrap after commas or before operators when a line is too long. Indent a wrapped continuation eight spaces more than its parent line. Keep a method or constructor name attached to its opening parenthesis.
* Use K&R braces. Braces are mandatory for every `if`, `else`, loop, and `try`/`catch` body, including one-line bodies.
* Put spaces around operators, after commas, after Java keywords, and after `for` semicolons. Separate logical units in a block with one blank line.
* Put every class in a package. Use explicit, minimal, consistently ordered imports; wildcard imports are forbidden.
* Attach array brackets to the type: `int[] values`, never `int values[]`.
* Declare and initialize variables at first use where possible. Declare each variable in the smallest scope that contains all its uses.
* Do not declare public mutable fields. Use private state and behavior or accessors; public constants are allowed.
* Every non-exhaustive traditional `switch` fall-through MUST have an explicit `// Fallthrough` comment. Prefer arrow-style switches when they make intent clearer.

## Markdown Source Convention

Markdown follows GitHub Flavored Markdown and the repository's Markdownlint configuration:

* Agents MUST NOT insert hard line breaks or reflow natural-language Markdown to meet a character limit. Keep every prose paragraph, list-item sentence, and blockquote paragraph on one physical source line. A prose line may be long. Preserve a line break only when Markdown syntax or an intentional author-controlled break requires it, such as a table row, fenced or indented code, a heading, a list boundary, or a deliberate hard break. Never apply a programming-language line-length rule to Markdown prose, and never rewrap adjacent lines after a local wording change. This prevents sentence fragments from confusing grammar tools and avoids unrelated line churn during edits.
* Put a blank line before lists and fenced code blocks, after headings, and between a heading and its content.
* Put a space after every heading marker and put `>` on every line of a blockquote.
* Use `1.` for every ordered-list item and `*` for unordered-list items.
* Use `_` rather than `*` for italics. Keep emphasis meaningful and do not over-format prose.
* Keep headings hierarchical and do not skip levels merely to obtain a visual size.
* Use relative links for repository files where possible. Verify links and image paths after moving or renaming files.
* Keep generated Markdown or website output out of hand-edited source when the repository has a source template or generator.

### Documentation and comments in Java

* Every public class and public method MUST have a descriptive Javadoc header, except getters/setters, test code, and overrides whose inherited contract applies exactly.
* Javadoc MUST state what the operation guarantees, document meaningful parameters and return values, and document thrown exceptions where useful.
* A method Javadoc opening `/**` MUST be on its own line. Its first sentence MUST be a short summary suitable for the generated method-summary table and index, and MUST begin with a third-person verb such as `Returns`, `Sends`, or `Adds`; do not use forms such as `Return` or `Returning`.
* In a multi-line Javadoc block, each subsequent `*` MUST align with the opening `*` and be followed by one space. Leave one empty Javadoc line between the description and any `@param`, `@return`, or `@throws` tags, but leave no blank source line between the closing `*/` and the documented declaration.
* Parameter and tag descriptions MUST end with punctuation. Document either every parameter with `@param` or none: omit all `@param` tags only when every parameter name is self-explanatory or already explained in the main description. Omit `@return` when a method returns nothing or its return value is obvious from the rest of the comment.
* An overriding method whose inherited contract applies exactly MAY use `@inheritDoc`; add local Javadoc only to document a behavior difference. A concise member Javadoc MAY be written on one line, for example `/** Number of connections to this database */`.
* Comments MUST explain what or why, not narrate obvious mechanics. Improve confusing code before adding a comment that explains how it works.
* Comments MUST be written for future readers, not as private notes about a temporary bug or the author's intentions.
* Non-trivial private methods and fields SHOULD have a concise explanatory header when their purpose or invariant is not obvious from their names.
* Delete dead code, stale comments, commented-out implementations, and unused imports in the same change that makes them obsolete.

## Code Quality And Defensive Programming

### SLAP and method design

* Each method MUST operate at one abstraction level. A controller may orchestrate a use case, but low-level parsing, file access, and view styling belong in named collaborators.
* Avoid methods longer than approximately 30 lines when extraction improves comprehension. Extract around a meaningful responsibility, not arbitrary line counts.
* Keep nesting at three levels or fewer where practical. Use guard clauses, early returns, named predicates, and extracted helpers to remove arrowhead code.
* Replace complex boolean expressions with named intermediate predicates when the condition cannot be understood at a glance.
* A class MUST have one coherent responsibility. Do not place UI validation, persistence mapping, and domain mutation in one class.

### Simplicity and duplication

* Follow KISS: choose the simplest correct design that satisfies the current requirement and existing architecture.
* Do not add abstractions, caching, concurrency, or optimization without a demonstrated requirement or measured bottleneck and a protecting test.
* Replace unexplained numeric, string, and other magic literals with named constants or domain types. A conventional literal may remain when its meaning is unambiguous in context.
* Do not copy-paste-modify production or test logic. Extract a helper or parameterize a test when the shared behavior is stable and the abstraction remains easier to read.
* Remove dead code as soon as it becomes redundant; version control is the recovery mechanism.

### Exceptions, assertions, logging, and defensive boundaries

* Use exceptions for unusual user or environment conditions, such as invalid input at a boundary, missing files, or failed persistence. Use normal return values for expected branching in normal workflow.
* Catch exceptions only where the layer can recover, translate, or present a useful message. Never use an empty `catch`; if an unavoidable no-op exists, explain its reason in a comment and preserve relevant diagnostic context.
* Never catch `Exception` or a broad superclass merely to suppress failure. Catch the narrowest meaningful type and either recover, wrap with context, or propagate it.
* User-facing errors MUST state what failed, why the input or operation was rejected when known, and what the user can do next. Error text in the User Guide and tests must match the product.
* Detect overflow and invalid numeric conversion at input boundaries. Handle a foreseeable malformed or oversized value safely rather than allowing a low-level exception, wraparound, data corruption, or unusable interface.
* Use assertions for programmer assumptions, class invariants, preconditions, postconditions, and impossible control-flow states. Assertions MUST NOT perform required work because assertions may be disabled.
* Use logging at meaningful application boundaries and failure paths. Logs should help diagnose startup, command, persistence, and unexpected failures without logging passwords, private student data, parent/guardian contact details, lesson content, or full user input.
* Enforce compulsory associations and non-null invariants at the domain boundary. Do not rely on every caller remembering a rule.
* Do not return mutable internal collections or expose mutable domain state. Return an immutable view, defensive copy, or a purpose-built operation.
* Validate external data before it enters the domain. A malformed or partially valid file MUST not silently corrupt valid in-memory data; report a recoverable error according to the Storage contract.

## Testing Convention

Testing is part of the feature, not cleanup after implementation. The course does not impose a numeric coverage minimum; the agent must instead justify test depth by risk and user impact.

### Required test design

For every behavior change, add or identify tests for:

* the normal successful path;
* each distinct invalid-input or rejected-operation path;
* foreseeable user mistakes at input boundaries, including missing separators, overflow, unusually long values, and symbols that resemble ordinary real-world data;
* empty, boundary, duplicate, missing, and repeated-use cases that the feature can encounter;
* persistence and reload behavior when data is stored;
* user-visible error text or result state when the contract specifies it;
* regression behavior in adjacent existing features.

Use the narrowest useful level first:

* Domain tests prove invariants and value-object behavior without JavaFX or file I/O.
* Logic/parser tests prove command syntax, validation, and result mapping.
* Storage tests use temporary files and prove serialization, malformed data, compatibility, and failure recovery.
* UI tests prove user-visible wiring and interaction only where the behavior cannot be proven at a lower boundary.
* Integration tests prove a complete vertical slice when multiple boundaries must work together.

Additional rules:

* Test names SHOULD use `featureUnderTest_testScenario_expectedBehavior` and state behavior rather than implementation details.
* Arrange, act, and assert phases MUST be visually clear. A test should have one primary reason to fail.
* Test fixtures MUST be deterministic, minimal, and free of realistic personal data. Use obviously synthetic names, contacts, and lesson notes.
* Mock external systems such as clocks, randomness, network, and file-system boundaries only where needed for determinism. Do not mock domain logic in the same bounded context merely to make a unit test easy.
* Do not use sleeps, current time, machine-specific paths, locale-dependent formatting, network access, or test-order dependence in deterministic tests.
* A regression test MUST be added for every confirmed bug whose behavior can be expressed automatically.
* When tests intentionally change because the specified behavior changed, explain the reason in the final handoff.

### Test and check loop

Before handoff, run the smallest relevant check, then the broader checks. A coding change must not be considered style-checked merely because one command passed:

1. Run the configured formatter, if one exists. This repository currently has no separate formatter command configured.
2. Run `./.beryl/scripts/check-md.sh` for Markdown sanity on every change that touches Markdown.
3. Run `npx --no-install markdownlint-cli2` on changed Markdown files when the local Markdownlint dependency is available.
4. Run `./gradlew checkstyleMain checkstyleTest` for every Java production or test change.
5. Run `./.beryl/scripts/check-affected.sh --worktree` and the narrowest relevant Gradle test task for behavior changes.
6. Run `./gradlew test` for behavior changes unless a documented environment limitation prevents it.
7. Run `./.beryl/scripts/check.sh` for the aggregate deterministic gate.
8. When a commit is explicitly authorized and Gitlint is installed, run `gitlint --config .gitlint --commits HEAD^..HEAD` after staging the commit.

If a check is not applicable, say why. If it is applicable but unavailable or blocked by the environment, report the exact command and failure instead of silently omitting it.

For a release or packaging change, also verify the executable JAR, Java 25 launch path, clean-folder startup, local data creation, and the documented artifact name. For UI changes, inspect at the required screen sizes and use the repository's browser-verification workflow when a web runtime exists.

## Documentation And Diagram Convention

### User Guide

When changing a current feature, update `docs/UserGuide.md` so that it:

* clearly names the target user and the product's value proposition;
* covers every current user-facing feature and matches actual behavior;
* gives a short normal-use explanation plus representative sample inputs and outputs where they improve usability;
* explains invalid input and recovery when a user is likely to encounter it;
* uses screenshots only when they clarify the explanation, crops them to the relevant area, and avoids repeated screenshots that are expensive to update;
* uses stable headings and links instead of relying on omitted section or figure numbers; and
* labels unimplemented future behavior as `Coming soon` instead of presenting it as available.

### Developer Guide

When changing implementation or architecture, update `docs/DeveloperGuide.md` so that it:

* matches the latest release and current source code;
* explains the relevant design at a high level before showing lower-level classes or code;
* includes an `Instructions for Manual Testing` appendix covering every new user-testable feature, with copy-pasteable important inputs and a path through the feature; and
* avoids repeating the User Guide when a link is enough.

If PlantUML is used, commit the source `.puml` file under `docs/diagrams` and regenerate or update the corresponding rendered asset according to the repository's documentation process. Never hand-edit a generated diagram when its source is available.

### Architecture and UML

* Use intuitive symbols and labels that a reader can understand without inspecting implementation details.
* Use single-headed arrows for direction and relationship intent. Use double-headed arrows only when bidirectionality is real and necessary.
* Keep architecture diagrams high-level. Move low-level fields, framework plumbing, and incidental methods to a more suitable detailed diagram.
* Use the UML notation taught in the course. Choose a diagram type that fits the question; do not use a complicated class diagram where a sequence, activity, component, or state diagram communicates better.
* Keep diagrams small enough to read. Split a diagram by concern when adding detail makes it harder to understand.
* Code snippets in documentation MUST be the smallest excerpt that proves the point and MUST compile conceptually against the current implementation.

### Requirements and glossary

* User stories MUST contain all three parts: a role, a capability, and a benefit. The three parts MUST describe the same behavior.
* Use cases MUST cover important user goals, use consistent formatting and step numbering, separate main flow from extensions, and avoid unnecessary UI implementation details.
* Non-functional requirements MUST be real, relevant, scoped, measurable or objectively checkable, and reasonably achievable.
* The glossary MUST contain important domain terms and MUST exclude terms that are not needed to understand the product. Use the project ubiquitous language consistently in code and documentation.
* The Developer Guide `Acknowledgements` section MUST identify reused or adapted code, ideas, documentation, and inspiration, with links and an accurate description of the extent of reuse.

## Git And Incremental Delivery

The agent must not commit or push unless the user explicitly authorizes it. When a commit is authorized:

* Create a complete, reviewable logical commit. Stage every change in the stated boundary and no unrelated change.
* Use one purpose per commit. Separate feature behavior, tests, documentation, generated output, and unrelated cleanup when they are independently reviewable.
* Write the commit message before committing. The subject MUST use imperative mood, begin with a capital letter, target a 50-character soft limit, never exceed 72 characters, and have no final period. An optional `scope:` or `category:` prefix is allowed.
* For non-trivial commits, add a body after one blank line. Body lines MUST be at most 72 characters, use present tense for the current situation, explain why the change is needed, state what to do in imperative mood, and explain relevant rationale without narrating obvious implementation mechanics.
* Use meaningful kebab-case branch names. Issue branches begin with the issue number followed by keywords from the issue title.
* Keep each increment buildable and testable. Do not hide a broken intermediate state inside a large final commit.
* Before committing, verify that staged files, the commit boundary, and the message all agree. Run the available Gitlint check when Gitlint is installed.

The agent MUST NOT fabricate milestones, releases, reviews, peer-testing results, task assignments, or individual-contribution evidence. It may prepare the artifacts and report the human follow-up required.

## Change Handoff Checklist

Before declaring a change complete, the agent MUST confirm:

* The user goal, target user, and affected bounded context are explicit.
* The feature is cohesive, keyboard-friendly where relevant, and within scope.
* Existing behavior and brownfield boundaries remain intact unless the change intentionally updates them.
* Domain, UI, logic, and persistence responsibilities remain separated.
* Naming, layout, Javadoc, SLAP, duplication, exceptions, assertions, logging, defensive boundaries, and privacy were reviewed.
* Automated tests cover normal, invalid, boundary, persistence, and regression behavior appropriate to the change.
* User Guide, Developer Guide, diagrams, glossary, and design records are synchronized with the implementation where applicable.
* Formatter/style checks, narrow checks, and `./.beryl/scripts/check.sh` were run, with skipped or unavailable checks recorded.
* No realistic personal data, secrets, private contact details, or sensitive lesson content was added to source, tests, screenshots, logs, or docs.
* The final report maps each changed file to a commit boundary, identifies any file outside a boundary, states whether tests changed, lists skipped checks, names the skills used, and says whether temporary session state was cleared.

## Source Of This Policy

This policy was reconciled against the course material on 30 September 2026. Re-check the live pages if the course site changes its rubric or constraints:

* [tP Grading](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-grading.html)
* [tP Constraints](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-constraints.html)
* [tP Expectations](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-expectations.html)
* [tP Deliverables](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-deliverables.html)
* [tP Practical Exam](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-pe.html)
* [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
* [CS2103/T Code Quality](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html)
* [CS2103/T Error Handling](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/errorHandling.html)
