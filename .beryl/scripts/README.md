# Beryl Scripts

The deterministic entry point is:

```bash
./.beryl/scripts/check.sh
```

It verifies Markdown, secrets, and affected project tests. The fast hook-safe form is `./.beryl/scripts/check.sh --fast`.

Focused commands remain available when needed:

* `check-md.sh` checks Markdown fences and tabs.
* `check-secrets.sh` scans staged or worktree changes for likely secrets.
* `check-affected.sh` selects the configured related or full project test command.

Configuration belongs in `.beryl/agent/affected-tests.conf`. Agent-memory validation is provided by `./.beryl/agent/scripts/agent-doctor.sh`.
