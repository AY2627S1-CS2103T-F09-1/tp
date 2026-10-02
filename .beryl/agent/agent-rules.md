# Agent Rules

## Start Here

1. Read `.beryl/agent/coding-policy.md` before changing code, tests, documentation, configuration, or Git history.
1. Read the smallest relevant project-context file: `project-brief.md`, `design-tree.md`, `architecture.md`, `ubiquitous-language.md`, `testing-policy.md`, or an ADR.
1. Select one workflow skill directly from `.beryl/agent/skills/`: `planning`, `adding-features`, `debugging`, or `explaining-codebase`. Use `grill-me` for risky or ambiguous design work.
1. Follow the selected skill and the coding policy. The coding policy wins when they conflict.

## Operating Defaults

* Feature implementation needs a user-ratified plan.
* Do not use sub-agents unless the user explicitly asks.
* Use `.beryl/agent/session-state.md` only for temporary state and clear it when work ends.
* Never weaken tests to make an implementation pass. If tests change intentionally, run `./.beryl/scripts/update-test-manifest.sh` and commit the updated manifest with them.
* After edits, run the formatter if configured, focused checks, and `./.beryl/scripts/check.sh`.
* Record durable architecture, vocabulary, or test-strategy decisions in the design tree or an ADR, not in temporary state.

## Completion

Report changed files and their commit boundary, checks run or skipped, design-record updates, test-manifest changes, and whether temporary state was cleared.
