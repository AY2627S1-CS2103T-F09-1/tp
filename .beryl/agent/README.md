# Agent Memory Layer

`.beryl/agent/` is the repository-owned memory layer for coding agents. Read `coding-policy.md` first; it is the primary contract for engineering practice, checks, documentation, security, and commits.

## Canonical Context

* `agent-rules.md`: concise navigation and operating defaults.
* `constraints.md`: mandatory course constraints and relevant recommendations.
* `project-brief.md`: product goal, users, workflows, and scope.
* `design-tree.md`, `architecture.md`, `ubiquitous-language.md`, and `adr/`: durable product and architecture knowledge.
* `testing-policy.md`: available checks and test-change rules.
* `skills/`: focused workflows for planning, approved implementation, debugging, explanation, pull-request review, and design critique.

## Documentation Ownership

Use the smallest document that answers the question:

* `docs/UserGuide.md` is the source of truth for current user-visible commands,
  syntax, examples, validation, errors, and recovery.
* `docs/DeveloperGuide.md` is the source of truth for current implementation
  flow, component interactions, persistence details, and manual testing.
* `architecture.md`, `design-tree.md`, `ubiquitous-language.md`, and `adr/`
  record durable boundaries, decisions, and terminology; they do not duplicate
  the complete command reference.

Do not create a separate command contract or feature specification when the
behavior is already covered by the User Guide and Developer Guide. Update the
owning document when behavior or design changes.

Tool-specific instruction files are generated shims. Change `tool-instruction-template.md`, then run:

```bash
./.beryl/agent/scripts/sync-agent-env.sh
```

Run `./.beryl/agent/scripts/agent-doctor.sh` to verify the installed memory layer and `./.beryl/scripts/check.sh` for the complete deterministic gate.
