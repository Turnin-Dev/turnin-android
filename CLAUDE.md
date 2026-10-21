# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Software Architecture Rules

### Layer Structure (Clean Architecture)
- Dependency direction: `data → domain ← presentation`. The domain layer must not import from any other layer.
- domain: Business models, UseCases, and Repository interfaces only. No dependency on frameworks, databases, or networks.
- data: Repository implementations and external data access. Convert external models ↔ domain models here.
- presentation: Handles user input/output. Depends only on domain, never directly on data.
- Convert models at layer boundaries. Never expose external models to inner layers.

### Design Principles (SOLID)
- SRP: A class or function has only one reason to change.
- OCP: Design so behavior can be extended without modifying existing code.
- LSP: Subtypes must be substitutable for their base types.
- ISP: Keep interfaces small and role-focused.
- DIP: Depend on abstractions, not concrete classes. Inject dependencies via constructors.

### Data Flow
- Maintain unidirectional flow: `Input (Event) → State change → State → Output`.
- Manage state immutably, and mutate it only through a single entry point.
- The output layer only renders state and contains no business logic.

### Error Handling
- Never swallow exceptions. Handle them, or convert them into a meaningful type and propagate.
- Convert external exceptions into domain error types in the data layer.
- Represent expected failures with types such as Result/Either; reserve exceptions for unexpected situations.

### Testability
- Inject all dependencies; avoid global state and direct singleton access.
- Abstract external factors (time, randomness, network) so they can be replaced in tests.
- Domain logic must be unit-testable without any framework.
- Write tests in Given-When-Then structure, with one behavior verified per test.

### General Code Principles
- KISS / YAGNI: Implement only what is needed now, as simply as possible.
- DRY: Remove duplication only when the meaning is the same; avoid premature abstraction.
- Keep functions small and single-purpose. Make side effects explicit.
- Extract magic numbers/strings into constants. Use comments only to explain "why".

### Naming Conventions
- Names reveal intent. Avoid abbreviations and vague names (`data`, `temp`, `manager`).
- Classes/types are nouns; functions start with verbs. Booleans start with `is/has/can`.
- Use consistent role suffixes: `~UseCase`, `~Repository`, `~DataSource`, `~Dto`.
- Follow the language's existing conventions (case style, etc.).

### Handling Exceptions
- These rules are defaults, not absolute laws. Pragmatic exceptions are allowed when following a rule would add unreasonable complexity or cost.
- Do not break a rule silently. When an exception seems warranted, explain which rule is affected and why, propose an alternative, and let me decide.
- If I approve an exception, keep it minimal in scope and note it with a short comment explaining the reason.

## Build & verify

- A root `local.properties` is required — Gradle configuration fails without it (Kakao keys, `*_TURNIN_SERVER_URL`, `*_GOOGLE_WEB_CLIENT_ID`, etc. are read into BuildConfig). `google-services.json` lives per build type in `app/src/{debug,release,releaseTest}/`. Never read or print these files.
- Before calling a change done, run the same sequence as CI (`.github/workflows/ci.yml`):
  `./gradlew ktlintCheck` → `./gradlew testDebugUnitTest` → `./gradlew assembleDebug`
- Sandboxed sessions: Gradle builds must run on a daemon started **outside** the Claude Code sandbox. A daemon spawned inside the sandbox cannot read `local.properties` or `build/{tmp,intermediates,outputs,generated}` (read-denied), so tasks fail with `Operation not permitted` (e.g. `:build-logic:convention:jar`). If you see that error, or the build reuses a sandboxed daemon, ask the user to run `! ./gradlew --stop && ./gradlew help`, then retry. Do not loosen the read-deny rules.
- ktlint is a custom JavaExec task in `ktlint.gradle.kts` (not a plugin); style is configured in `.editorconfig` (ktlint_official, trailing commas, no star imports). Fix with `./gradlew ktlintFormat`.
- Single module tests: `./gradlew :presentation:testDebugUnitTest` (or `:core:data:`, `:domain:test` for the pure-JVM modules). `:shared:*` modules run Android host tests via `testAndroidHostTest` (`turnin.kmp.library` and `turnin.jvm.library` register a `testDebugUnitTest` alias so CI runs them). iOS check (needs Xcode): `./gradlew :shared:domain:iosSimulatorArm64Test :shared:data:iosSimulatorArm64Test :shared:iosSimulatorArm64Test`.
- iOS app build: `xcodebuild` with a concrete simulator destination (e.g. `-destination 'platform=iOS Simulator,name=iPhone 17'`). `generic/platform=iOS Simulator` also builds x86_64, which the framework doesn't target (arm64 only), so SKIE Swift types fail to resolve.
- Build types: `debug`, `release`, `releaseTest` (release config + debug signing; `IS_RELEASE_TEST=true` points release at debug servers). No product flavors.

## Architecture

- Clean Architecture: `:presentation` / `:domain` / `:data` hold all features as packages (home, friend, profile, …); shared code is in `:core:{designsystem,presentation,domain,data,common}`. `:domain` and `:core:domain` are pure JVM — no Android imports there.
- KMP shared features (strategy: `docs/spec/kmp-migration-strategy-v2.md`): features used by both Android and iOS move into `:shared:domain` / `:shared:data` / `:shared:presentation` (layers enforced by module deps: data → domain ← presentation) plus the `:shared` umbrella (composition root: `sharedModules`, iOS entry point, framework export). Packages are `com.turnin.shared.<layer>.core.*` and `com.turnin.shared.<layer>.feature.<feature>.*`; features never import each other and `core` never imports `feature` (enforced by `SharedPackageBoundaryTest`). Android may depend on shared only as `:presentation`/`:core:presentation` → `:shared:domain`, `:core:data` → `:shared:data`, `:app` → `:shared` — never make `:domain` or `:core:domain` depend on `:shared`. Shared code has plain constructors (no `@Inject`) and is wired with Koin: data modules in `:shared:data` `feature/<feature>/di/`, UseCase registration in the umbrella `di/XxxModule.kt` added to `sharedModules`. Android gets them through the single Koin container in `app/.../di/SharedKoinBridge.kt` plus a per-feature `SharedXxxModule` (`@Provides fun provideXxxUseCases(koin: Koin) = koin.get()`); new platform inputs go into `SharedKoinBridge` with a `SharedQualifier` and an explicit type. Create at most one Koin container per platform (`single` instances live in the `Module` objects) and close it before creating another, including in tests. Follow the feature move checklist in the strategy doc.
- Retry/timeout values and retryable-error rules live only in `NetworkPolicy` (`:shared:data`), used by both Ktor and Retrofit (`retry()`, `networkCall`) — never redeclare them. Keep Ktor at 3.5.x while on AGP 8.13 (newer Ktor pulls OkHttp ≥5.4, which needs compileSdk ≥36/37).
- iOS: `iosApp/` (SwiftUI) uses the static `Shared` framework built by `embedAndSignAppleFrameworkForXcode`; only `:shared:presentation` is exported. `SharedEntryPoint` (umbrella iosMain, an approved global-singleton exception) only starts the Koin container (`start(baseUrl:)`); feature APIs are Facades in `:shared:presentation` iosMain that convert domain results into non-generic sealed/enum/primitive types (`XxxUiState`, `UiXxx`, `XxxErrorReason`), registered in `iosModules`, and Swift gets them with `SharedEntryPoint.inject()` — never grow `SharedEntryPoint` per feature. SKIE turns `Flow`/sealed/suspend into `AsyncSequence`/`enum`/`async` (analytics disabled — keep it off). Server URL comes from `iosApp/Config.xcconfig` (git-ignored; never read it). iOS tokens use `installBearerAuth` + `KeychainTokenStorage`; Android keeps `DataStoreManager` + OkHttp `TokenAuthenticator` (Ktor reuses `@TokenOkHttpClient` via `preconfigured`). Keychain is unavailable in Kotlin/Native unit tests, so verify it in the app.
- Convention plugins in `build-logic/convention` (`turnin.android.library(.compose)`, `turnin.jvm.library`, `turnin.kmp.library`, …) — use them for new modules instead of raw AGP config.
- Navigation: type-safe `@Serializable` routes in the sealed `Route` hierarchy in `core/presentation/.../common/navigation/NavigationGraph.kt`. Every route must override `analyticsName` (enforced by `RouteAnalyticsNameTest`). See `docs/decisions/dl-4-screen-analytics.md`.
- Feature package layout (see `presentation/.../friend/` as reference): `XxxRoute.kt` (gets VM via `hiltViewModel()`, `collectAsStateWithLifecycle`, one-off effects via `ObserveAsEvents(viewModel.effect)`), `XxxNavigation.kt`, `viewmodel/`, `state/` (UiState/Effect; effects sent via `Channel` + `receiveAsFlow`), `model/` (`UiXxx` + `toUiModel()`), `view/` (`XxxScreen`), `error/` (`XxxErrorTypeAsUiText.kt`).
- Use cases: `XxxUseCase` with `operator fun invoke()`, usually returning `Flow<Result<T, XxxErrorType>>` (emit `Result.Loading` first, `.catch { emit(Result.Error(XxxErrorType.Unexpected(it))) }`). Group them in an `XxxUseCases` aggregator with KDoc'd vals; ViewModels inject the aggregator.
- Errors follow `ERROR_STRATEGY.md`: `Result` (core/domain), `NetworkResult` (core/data), `ValidationResult`; per-feature `XxxErrorType` in `domain/<feature>/error/`, mapped to `UiText` in presentation. Shared features use their own `Result` and `NetworkErrorType` from `:shared:domain` (`core/result`, `core/error`) and `XxxErrorType` in `:shared:domain` `feature/<feature>/error/`; ViewModels that handle both import it as `import com.turnin.shared.domain.core.result.Result as SharedResult`. Map shared `NetworkErrorType` to UI text with `asUiText()` from `NetworkErrorTypeAsUiText.kt` (`:core:presentation`).
- Use `TurninTheme` tokens (`TurninTheme.colorScheme`, typography) from `:core:designsystem`, not raw Material colors.
- Room: schema changes require bumping `TurninDatabase.DATABASE_VERSION`, adding a migration (auto-migration if possible), and committing the exported JSON in `core/data/schemas/`.

## Language

- Commit messages, KDoc/comments, and PR descriptions are written in Korean.
- String resources are Korean with snake_case keys prefixed by screen/feature (e.g. `login_screen_btn_google`, `common_btn_cancel`).

## Git

- Branch: `<type>/PK-<n>-<desc>` (e.g. `feat/PK-153-add-event`), branched from and PR'd into `develop`. `develop` → `main` triggers release CD (Firebase App Distribution + Play alpha) — never PR features to `main`.
- Commits: `[PK-<n>] <type>: <Korean subject>` — JIRA key in brackets first, then a Conventional Commits type (e.g. `[PK-143] feat: 공지 유스케이스 추가`, `[PK-143] refactor(사용자 프로필): ...`). Every commit and PR title must include the PK JIRA key.
- After creating a new file, stage it with `git add <file>`. Never commit unless the user explicitly asks or approves the commit.
- PRs use the Korean template in `.github/pull_request_template.md`.
- Never bump `projectVersionName` / `projectVersionCode` in `gradle/libs.versions.toml` unless explicitly asked. But if a change looks like it warrants a version bump (e.g. preparing a release, or a `develop` → `main` PR), proactively suggest it — the user may forget.
