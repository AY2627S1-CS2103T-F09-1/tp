# Code Quality Guidelines

Use this checklist when changing production Java code. It is a project-local, actionable summary of the CS2103 code-quality guidance.

## Readability

* Keep methods short enough to understand at once; extract a named helper when it clarifies one responsibility or one level of abstraction.
* Keep nesting shallow. Use guard clauses when they make the normal path clear.
* Break complicated expressions into well-named intermediate values.
* Replace unexplained numeric or string literals with named constants, except when a conventional literal is clearer in context.
* Make intent explicit with braces, domain types, and descriptive names rather than clever or implicit constructs.
* Lay related work out in the order a reader experiences the behaviour; do not mix high-level orchestration with low-level detail.

## Simplicity And Domain Integrity

* Prefer the simplest correct design. Do not add abstractions or optimisations without a demonstrated need.
* Use TutorTrack terms from `ubiquitous-language.md` for new product code.
* Give each class one clear responsibility. Keep domain validation out of UI controllers and file-format conversion out of domain objects.
* Avoid unused parameters, confusingly similar names, multiple statements on one line, and values that are immediately overwritten.
* Keep public APIs minimal. Do not expose mutable state or another context's internals.

## Brownfield Discipline

* Preserve existing AddressBook behaviour unless a ratified TutorTrack feature explicitly changes it.
* Prefer an additive, tested migration path over broad opportunistic rewrites.
* Keep compatibility, persistence, and UI replacement decisions documented in the design tree or an ADR before implementation.
* Never use realistic student, parent, guardian, or lesson information in fixtures, screenshots, logs, or committed sample data.

## Review Requirement

Review every changed production file against this checklist. A code-quality increment is behaviour-preserving unless it also has an explicit feature or bug requirement and protecting tests.
