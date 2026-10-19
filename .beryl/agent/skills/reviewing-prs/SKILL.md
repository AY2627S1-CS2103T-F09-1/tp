# Reviewing Pull Requests

## Purpose

Review a pull request constructively, with precise, actionable feedback that helps the author and future maintainers understand the concern.

## Use When

* The user asks to review a pull request, branch, diff, or proposed change.
* The user asks for automated or agent-assisted pull-request feedback.

## Process

1. Identify the change's stated purpose and inspect the relevant diff, tests, and affected documentation.
1. Report only substantiated findings, ordered by impact, and attach each finding to the narrowest relevant code location.
1. Explain the concern and its consequence; ask a question or suggest an alternative where that encourages productive discussion.
1. State any remaining assumptions, checks not run, and positive observations that are specific and useful.

## Comment Style

* Add specific comments at the relevant code location instead of relying solely on an overall pull-request comment. Link multiple lines when the concern spans them.
* Use GitHub-Flavored Markdown where it improves clarity.
* Prefer questions for peer-review feedback, such as “Could this be extracted?” rather than directives.
* Phrase opinions personally and constructively: “I like how this separates X from Y” or “I am not sure this separation handles Y,” rather than declaring code simply good, bad, correct, or wrong.
* Ask for rationale when it would clarify a design choice, and suggest alternatives for the author to consider.
* Acknowledge clean or thoughtful work when the observation is specific and helpful.
* Prefer a respectful question such as “Perhaps a more intuitive variable name here?” over an overused “please.”
* Do not repeat the same feedback on every occurrence. After a few representative comments, note that the pattern appears elsewhere.
* Write in clear, regular English for all readers of the pull request. Avoid slang, colloquialisms, and culture-specific references.

## Review Output

Present findings first, each with its location and impact. If there are no findings, say so explicitly and list any residual risks or unverified areas. Keep the final summary concise.
