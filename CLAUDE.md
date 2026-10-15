# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & verify

- A root `local.properties` is required — Gradle configuration fails without it (Kakao keys, `*_TURNIN_SERVER_URL`, `*_GOOGLE_WEB_CLIENT_ID`, etc. are read into BuildConfig). `google-services.json` lives per build type in `app/src/{debug,release,releaseTest}/`. Never read or print these files.
- Before calling a change done, run the same sequence as CI (`.github/workflows/ci.yml`):
  `./gradlew ktlintCheck` → `./gradlew testDebugUnitTest` → `./gradlew assembleDebug`
- Sandboxed sessions: Gradle builds must run on a daemon started **outside** the Claude Code sandbox. A daemon spawned inside the sandbox cannot read `local.properties` or `build/{tmp,intermediates,outputs,generated}` (read-denied), so tasks fail with `Operation not permitted` (e.g. `:build-logic:convention:jar`). If you see that error, or the build reuses a sandboxed daemon, ask the user to run `! ./gradlew --stop && ./gradlew help`, then retry. Do not loosen the read-deny rules.
- ktlint is a custom JavaExec task in `ktlint.gradle.kts` (not a plugin); style is configured in `.editorconfig` (ktlint_official, trailing commas, no star imports). Fix with `./gradlew ktlintFormat`.
- Single module tests: `./gradlew :presentation:testDebugUnitTest` (or `:core:data:`). KMP modules run host tests via `testAndroidHostTest`; `turnin.kmp.library` also registers a `testDebugUnitTest` alias so CI runs them. iOS check (needs Xcode): `./gradlew :domain:iosSimulatorArm64Test :core:domain:iosSimulatorArm64Test`.
- Build types: `debug`, `release`, `releaseTest` (release config + debug signing; `IS_RELEASE_TEST=true` points release at debug servers). No product flavors.

## Architecture

- Clean Architecture: `:presentation` / `:domain` hold all features as packages (home, friend, profile, …); shared code is in `:core:{designsystem,presentation,domain,data,common}`. All repository implementations currently live in `:core:data` (no feature-level data module yet; future feature modules will be `:feature:x:{domain,data,presentation}`). `:domain` and `:core:domain` are KMP modules (`turnin.kmp.library`: android + iosArm64 + iosSimulatorArm64); code lives in `commonMain` — no Android/JVM-only APIs (`java.*`, `javaClass`, `javax.inject`), and `@JvmInline` needs an explicit `import kotlin.jvm.JvmInline`. `:core:data` is also KMP (`turnin.kmp.android.library`: `kotlin.multiplatform` + `com.android.library`, kept for build types/`BuildConfig`/Hilt): Android code lives in `androidMain`, unit tests in `androidUnitTest`, and annotation processors use `kspAndroid`/`kspAndroidTest` instead of `ksp`/`kspTest`. Domain tests are in `commonTest` using `kotlin.test` + Mokkery (MockK is JVM-only); on Kotlin/Native, test names can't contain `()` and JVM APIs like `String.toByteArray()` are unavailable (use `encodeToByteArray()`).
- Convention plugins in `build-logic/convention` (`turnin.android.library(.compose)`, `turnin.kmp.library`, `turnin.kmp.android.library`, `turnin.jvm.library`, …) — use them for new modules instead of raw AGP config.
- Navigation: type-safe `@Serializable` routes in the sealed `Route` hierarchy in `core/presentation/.../common/navigation/NavigationGraph.kt`. Every route must override `analyticsName` (enforced by `RouteAnalyticsNameTest`). See `docs/decisions/dl-4-screen-analytics.md`.
- Feature package layout (see `presentation/.../friend/` as reference): `XxxRoute.kt` (gets VM via `hiltViewModel()`, `collectAsStateWithLifecycle`, one-off effects via `ObserveAsEvents(viewModel.effect)`), `XxxNavigation.kt`, `viewmodel/`, `state/` (UiState/Effect; effects sent via `Channel` + `receiveAsFlow`), `model/` (`UiXxx` + `toUiModel()`), `view/` (`XxxScreen`), `error/` (`XxxErrorTypeAsUiText.kt`).
- Use cases: `XxxUseCase` with `operator fun invoke()`, usually returning `Flow<Result<T, XxxErrorType>>` (emit `Result.Loading` first, `.catch { emit(Result.Error(XxxErrorType.Unexpected(it))) }`). Group them in an `XxxUseCases` aggregator with KDoc'd vals; ViewModels inject the aggregator. Use cases have plain constructors (no `@Inject` — `javax.inject` is unavailable in KMP commonMain); register every new use case with `@Provides` in `app/src/main/java/com/turnin/app/di/<feature>/XxxUseCaseModule.kt`.
- Shared (KMP) network code lives in `:core:data` commonMain: build clients with `createHttpClient(engine, baseUrl)` and wrap calls with `ktorNetworkCall` / `ktorNetworkCallWithoutResponse` (returns `NetworkResult`). Retry/timeout values and retryable-error rules live only in `NetworkPolicy` (commonMain), shared by Retrofit `retry()`/`networkCall` and Ktor — never redeclare them. Shared objects are wired in the Koin `commonDataModule` and reach Hilt only through `CommonDataKoinBridge` (androidMain) — keep exactly one `CommonDataKoinBridge`; when it needs more platform inputs, add parameters to `provideCommonDataKoinBridge` and register them with a `PlatformQualifier` and an explicit type (`single<CoroutineDispatcher>(PlatformQualifier.IoDispatcher) { … }`). Test-only deps of `:core:data` go in `commonTest` (commonMain `implementation` deps are not on the Android unit-test classpath). Keep Ktor at 3.5.x while on AGP 8.13 — newer Ktor pulls OkHttp ≥5.4, which needs compileSdk ≥36/37.
- Errors follow `ERROR_STRATEGY.md`: `Result` (core/domain), `NetworkResult` (core/data), `ValidationResult`; per-feature `XxxErrorType` in `domain/<feature>/error/`, mapped to `UiText` in presentation.
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
