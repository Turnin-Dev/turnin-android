# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & verify

- A root `local.properties` is required — Gradle configuration fails without it (Kakao keys, `*_TURNIN_SERVER_URL`, `*_GOOGLE_WEB_CLIENT_ID`, etc. are read into BuildConfig). `google-services.json` lives per build type in `app/src/{debug,release,releaseTest}/`. Never read or print these files.
- Before calling a change done, run the same sequence as CI (`.github/workflows/ci.yml`):
  `./gradlew ktlintCheck` → `./gradlew testDebugUnitTest` → `./gradlew assembleDebug`
- Sandboxed sessions: Gradle builds must run on a daemon started **outside** the Claude Code sandbox. A daemon spawned inside the sandbox cannot read `local.properties` or `build/{tmp,intermediates,outputs,generated}` (read-denied), so tasks fail with `Operation not permitted` (e.g. `:build-logic:convention:jar`). If you see that error, or the build reuses a sandboxed daemon, ask the user to run `! ./gradlew --stop && ./gradlew help`, then retry. Do not loosen the read-deny rules.
- ktlint is a custom JavaExec task in `ktlint.gradle.kts` (not a plugin); style is configured in `.editorconfig` (ktlint_official, trailing commas, no star imports). Fix with `./gradlew ktlintFormat`.
- Single module tests: `./gradlew :presentation:testDebugUnitTest` (or `:core:data:`, `:domain:test` for the pure-JVM modules).
- Build types: `debug`, `release`, `releaseTest` (release config + debug signing; `IS_RELEASE_TEST=true` points release at debug servers). No product flavors.

## Architecture

- Clean Architecture: `:presentation` / `:domain` / `:data` hold all features as packages (home, friend, profile, …); shared code is in `:core:{designsystem,presentation,domain,data,common}`. `:domain` and `:core:domain` are pure JVM — no Android imports there.
- Convention plugins in `build-logic/convention` (`turnin.android.library(.compose)`, `turnin.jvm.library`, …) — use them for new modules instead of raw AGP config.
- Navigation: type-safe `@Serializable` routes in the sealed `Route` hierarchy in `core/presentation/.../common/navigation/NavigationGraph.kt`. Every route must override `analyticsName` (enforced by `RouteAnalyticsNameTest`). See `docs/decisions/screen-analytics.md`.
- Feature package layout (see `presentation/.../friend/` as reference): `XxxRoute.kt` (gets VM via `hiltViewModel()`, `collectAsStateWithLifecycle`, one-off effects via `ObserveAsEvents(viewModel.effect)`), `XxxNavigation.kt`, `viewmodel/`, `state/` (UiState/Effect; effects sent via `Channel` + `receiveAsFlow`), `model/` (`UiXxx` + `toUiModel()`), `view/` (`XxxScreen`), `error/` (`XxxErrorTypeAsUiText.kt`).
- Use cases: `XxxUseCase` with `operator fun invoke()`, usually returning `Flow<Result<T, XxxErrorType>>` (emit `Result.Loading` first, `.catch { emit(Result.Error(XxxErrorType.Unexpected(it))) }`). Group them in an `XxxUseCases` aggregator with KDoc'd vals; ViewModels inject the aggregator.
- Errors follow `ERROR_STRATEGY.md`: `Result` (core/domain), `NetworkResult` (core/data), `ValidationResult`; per-feature `XxxErrorType` in `domain/<feature>/error/`, mapped to `UiText` in presentation.
- Use `TurninTheme` tokens (`TurninTheme.colorScheme`, typography) from `:core:designsystem`, not raw Material colors.
- Room: schema changes require bumping `TurninDatabase.DATABASE_VERSION`, adding a migration (auto-migration if possible), and committing the exported JSON in `core/data/schemas/`.

## Language

- Commit messages, KDoc/comments, and PR descriptions are written in Korean.
- String resources are Korean with snake_case keys prefixed by screen/feature (e.g. `login_screen_btn_google`, `common_btn_cancel`).

## Git

- Branch: `<type>/PK-<n>-<desc>` (e.g. `feat/PK-153-add-event`), branched from and PR'd into `develop`. `develop` → `main` triggers release CD (Firebase App Distribution + Play alpha) — never PR features to `main`.
- Commits: Conventional Commits with Korean subjects (`feat: 로그인/회원가입 분석 이벤트 추가`, `refactor(사용자 프로필): ...`). Every commit and PR title must include the PK JIRA key.
- After creating a new file, stage it with `git add <file>`. Never commit unless the user explicitly asks or approves the commit.
- PRs use the Korean template in `.github/pull_request_template.md`.
- Never bump `projectVersionName` / `projectVersionCode` in `gradle/libs.versions.toml` unless explicitly asked. But if a change looks like it warrants a version bump (e.g. preparing a release, or a `develop` → `main` PR), proactively suggest it — the user may forget.
