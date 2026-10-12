# CS2103/T Constraints

Follow the current [tP constraints](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-constraints.html). This file records their operative details for agents; consult the course page again before a release because course requirements can change.

## Mandatory Constraints

* Evolve the supplied codebase. Major changes are permitted only as small, working increments.
* Target a single user who types quickly and prefers typing. Do not create a multi-user product, support separate people using a shared computer at different times, or support regular access to one user's data file by another user.
* Deliver breadth-first increments at a reasonably consistent rate across the project; do not defer the project to a short burst of work.
* Store data locally in a human-editable text file and retain at least AB3's level of manual-editing support.
* Read and write only inside the folder containing the JAR and its subfolders. Do not change operating-system settings.
* Do not use a DBMS. Use object-oriented design primarily; mix in another style only when justified.
* Support Windows, Linux, and macOS. Avoid deliberately using platform-dependent libraries or features.
* Support a computer with Java 25 and no other Java version installed. Do not require an installer.
* Do not depend on a team-operated remote server.
* Before using a third-party framework, library, or service, post the request in the course forum and obtain teaching-team approval, unless that specific dependency has already been approved for another team. Dependencies must be free, open source when they are not services, permissively licensed, require no installation by product users, and obey every other constraint. Avoid requiring users to create third-party-service accounts.
* Make the GUI work without resolution-related inconvenience at 1920x1080 or higher at 100% and 125% scaling, and keep every function usable at 1280x720 or higher at 150% scaling.
* Package the product as one JAR; use one ZIP containing the JAR and required files only when a JAR alone is impossible. Keep the product JAR/ZIP at or below 100 MB. Do not add unnecessary or overly high-quality assets or dependencies, because unnecessary JAR bloat is reportable as a bug. Treat JavaFX WebView as a significant size risk and do not use it merely to display the User Guide.

## Recommendations

* When using a public network API, provide a fallback for unavailable network access. Obtain NUS IT approval before using NUS data.
* Avoid features that are difficult to test manually or automatically. In particular, avoid heavy remote-API dependence, audio features, and account, login, or logout workflows unless their testability is justified and protected.
* Keep input primarily CLI-based, with the GUI used mainly for visual feedback. Provide CLI alternatives for applicable mouse/GUI actions, favor ordinary typing over key combinations, and pair a multi-step novice workflow with a one-shot equivalent for regular users where practical.
* Keep the target use realistic for a small standalone desktop application. Seek clarification in the forum, or from the professor, before proceeding when compliance is uncertain.
