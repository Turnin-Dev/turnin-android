---
name: verify
description: Run the same checks as CI (ktlintCheck, debug unit tests, assembleDebug) and report failures. Use before declaring a change done or before opening a PR.
---

Run these in order from the repo root, stopping at the first failure:

1. `./gradlew ktlintCheck`
   - On failure: run `./gradlew ktlintFormat`, then re-run `ktlintCheck`. Report anything it could not auto-fix with file:line.
2. `./gradlew testDebugUnitTest`
   - On failure: list each failing test class/method and the assertion message. Read the reports under `<module>/build/reports/tests/` if console output is truncated.
3. `./gradlew assembleDebug`

If `local.properties` is missing, stop and tell the user — the build cannot configure without it. Do not create or read it.

Finish with a short pass/fail line per step. Don't claim success for a step that didn't run.
