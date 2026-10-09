---
name: new-screen
description: Scaffold a new feature screen following this repo's conventions (Route/Navigation, ViewModel, state, view, error mapping, domain ErrorType and UseCases, navigation Route with analyticsName). Use when adding a new screen or feature package.
---

Arguments: $ARGUMENTS — the feature name (e.g. `Bookmark`) and a short description of what the screen does.

Before writing anything, read the reference feature end to end so the output matches current code, not this summary:
- `presentation/src/main/java/com/turnin/presentation/friend/` (Route, Navigation, viewmodel, state, model, view, error)
- `domain/src/main/java/com/turnin/domain/friend/` (error, usecase incl. `FriendUseCases`)
- `core/presentation/src/main/java/com/turnin/core/presentation/common/navigation/NavigationGraph.kt`
- `ERROR_STRATEGY.md` and `docs/decisions/screen-analytics.md`

Then create (package names in lowerCamelCase, e.g. `keywordDetail`):

**Domain** (`domain/.../<feature>/`, pure Kotlin — no Android imports)
- `error/<Name>ErrorType.kt` — sealed type implementing the base error, including `Unexpected(throwable)`.
- `usecase/<Action>UseCase.kt` — `operator fun invoke()` returning `Flow<Result<T, <Name>ErrorType>>`: emit `Result.Loading` first, `.catch { emit(Result.Error(<Name>ErrorType.Unexpected(it))) }`.
- `usecase/<Name>UseCases.kt` — `@Inject constructor` aggregator with Korean KDoc on each val.

**Presentation** (`presentation/.../<feature>/`)
- `<Name>Route.kt` — `hiltViewModel()`, `collectAsStateWithLifecycle`, `ObserveAsEvents(viewModel.effect)`; passes navigation lambdas in.
- `<Name>Navigation.kt` — `NavGraphBuilder` extension + `NavController` navigate function, same shape as `FriendNavigation.kt`.
- `viewmodel/<Name>ViewModel.kt` — `@HiltViewModel`, injects `<Name>UseCases` (and `SnackbarController` if it shows messages); effects via `Channel` + `receiveAsFlow()`.
- `state/` — UiState and Effect types.
- `view/<Name>Screen.kt` — stateless composable using `TurninTheme` tokens, with a `@Preview` wrapped in `TurninTheme`.
- `error/<Name>ErrorTypeAsUiText.kt` — `asUiText()` mapping.
- Strings in `presentation/src/main/res/values/strings.xml`, Korean, keys prefixed `<feature>_screen_...`.

**Navigation**
- Add a `@Serializable` entry to the sealed `Route` in `NavigationGraph.kt`, overriding `analyticsName`.
- Wire it into `app/src/main/java/com/turnin/app/navigation/AppNavigation.kt` (or `BottomNavigation.kt` for a tab).

**Tests**
- Add unit tests for each UseCase (`domain/src/test/...`) and the ViewModel (`presentation/src/test/...`), using MockK + coroutines-test and the fixtures in `core/presentation/src/testFixtures` — mirror the existing friend tests.

Skip any data-layer/API work unless the user describes the endpoint; ask instead of inventing one. Finish by running `/verify`.
