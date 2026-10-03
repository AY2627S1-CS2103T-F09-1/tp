# Agent Memory Layer

`.beryl/agent/` is the repository-owned memory layer for coding agents. Read `coding-policy.md` first; it is the primary contract for engineering practice, checks, documentation, security, and commits.

## Canonical Context

* `agent-rules.md`: concise navigation and operating defaults.
* `project-brief.md`: product goal, users, workflows, and scope.
* `design-tree.md`, `architecture.md`, `ubiquitous-language.md`, and `adr/`: durable product and architecture knowledge.
* `testing-policy.md`: available checks and test-change rules.
* `skills/`: focused workflows for planning, approved implementation, debugging, explanation, and design critique.

Tool-specific instruction files are generated shims. Change `tool-instruction-template.md`, then run:

```bash
./.beryl/agent/scripts/sync-agent-env.sh
```

Run `./.beryl/agent/scripts/agent-doctor.sh` to verify the installed memory layer and `./.beryl/scripts/check.sh` for the complete deterministic gate.
