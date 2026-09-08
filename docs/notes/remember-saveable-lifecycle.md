# 노트: rememberSaveable과 회전(configuration change) 동작 원리

- 작성일: 2026-09-07
- 계기: `ScreenViewTracker`의 `isRotated` 플래그가 리셋되지 않아, 회전 후
  같은 화면(바텀 네비게이션 destination)에 재진입해도 로그가 영구적으로
  찍히지 않는 버그를 디버깅하며 정리함.

## 1. Activity 재생성 시 콜백 순서 (일반 원칙)

```
onPause() -> onSaveInstanceState() -> onStop() -> onDestroy()
```

`onSaveInstanceState()`가 `onDestroy()`보다 항상 먼저 호출된다.
→ `onDestroy()` 근처(`DisposableEffect.onDispose` 등)에서 값을 바꿔도,
**이 값이 Bundle을 거쳐 저장/복원되는 경우**라면 이미 스냅샷이 끝난 뒤라
반영되지 않을 수 있다. (아래 2번 케이스와 구분 필요)

## 2. `rememberSaveable`이 항상 Activity의 Bundle을 거치는 건 아니다

Navigation Compose에서 각 destination(`NavBackStackEntry`)은 그 자체가
`LifecycleOwner`, `ViewModelStoreOwner`, `SavedStateRegistryOwner`이다.
`NavBackStackEntry.LocalOwnersProvider`가 이 entry 자신을
`LocalViewModelStoreOwner`/`LocalLifecycleOwner`/`LocalSavedStateRegistryOwner`로
`composable<T> { }` 콘텐츠에 제공하고, `SaveableStateHolder`로 콘텐츠의
저장 가능한 상태를 관리한다 (공식 API 문서, `LocalOwnersProvider`).

즉 `composable<T> { }` 블록 안에서 쓴 `rememberSaveable`은 **Activity의
SavedStateRegistry가 아니라, 그 destination에 해당하는 `NavBackStackEntry`
자신의 SavedStateRegistry**에 저장된다.

공식 문서는 이 세 객체(Lifecycle, ViewModelStore, SavedStateRegistry)의
생존 범위를 이렇게 명시한다: "이 객체를 통해 제공되는 Lifecycle,
ViewModelStore, SavedStateRegistry는 이 destination이 백스택에 있는 동안
유효하다. 이 destination이 백스택에서 제거되면 lifecycle이 destroy되고,
상태는 더 이상 저장되지 않으며, ViewModel들이 clear된다."
(`NavBackStackEntry` 레퍼런스 문서)

- 핵심은 **"Activity 생명주기"가 아니라 "destination이 백스택에 있는지
  여부"에 스코프**되어 있다는 점이다.
- 회전은 Activity를 파괴/재생성할 뿐 **백스택 자체는 유지**되므로, entry와
  entry의 SavedStateRegistry도 파괴되지 않고 살아있는 객체로 이어진다.
  이는 `ViewModelStore`가 configuration change에서 살아남는 것과 동일한
  메커니즘(retained/NonConfigurationInstance)이며, `NavBackStackEntry`도
  `ComponentActivity`, `Fragment`와 함께 `ViewModelStoreOwner` 구현체
  목록에 포함된다 (공식 `ViewModelStore` 문서).
- 실제 디버깅 로그로 확인한 결과, 회전 전후로 `rememberSaveable`의
  초기화 로그가 다시 찍히지 않고(=새 객체가 아님), `onDispose`에서 세팅한
  값이 지연 없이 그대로 이어졌다. 위 메커니즘과 정확히 일치하는 관찰이다.

**교훈**: "onSaveInstanceState가 먼저 실행되니 onDispose에서 바꾼 값은
저장 안 될 것"이라는 가정(1번 원칙)은 Activity의 Bundle 저장/복원 경로를
탈 때만 유효하다. Navigation Compose destination 내부의 `rememberSaveable`은
Activity가 아니라 **entry 스코프의 SavedStateRegistry**를 타므로, 이
가정이 적용되지 않는다. 상태가 실제로 어떤 경로(Activity Bundle vs
entry-scoped SavedStateRegistry)를 타는지 확인하지 않으면 틀린 결론을
내리기 쉽다.

## 3. 진짜 원인은 "리셋 로직 누락"

```kotlin
DisposableEffect(screenName) {
    onDispose {
        if (activity?.isChangingConfigurations == true) {
            isRotated = true   // 세팅은 있는데
        }
    }
}
// isRotated를 다시 false로 되돌리는 코드가 어디에도 없었음
```

`isRotated`는 "회전 직후 1회만 로그를 건너뛰기 위한 일회성 신호"로 설계된
것이었지만, 이를 소비한 뒤 리셋하는 코드가 없었다. 동일 destination의 상태
객체가 (2번 이유로) 재방문 때도 계속 유지되므로, 한 번 `true`가 되면
영구적으로 `true`인 채 남아 이후 재방문에서도 로그가 찍히지 않았다.

## 4. 수정

```kotlin
LaunchedEffect(screenName) {
    if (!isRotated) {
        analyticsTracker.logEvent(AnalyticsEvent.ScreenView(screenName))
    }
    isRotated = false // 소비했으면 즉시 리셋 (다음 재방문을 위해)
}
```

## 5. 디버깅 팁

증상만으로 원인을 추측하지 말고, 각 지점(초기화/현재값/onDispose 진입)에
타임스탬프 로그를 심어 **실제 실행 순서**를 직접 확인하는 것이 가장
빠르고 확실했다.

```kotlin
rememberSaveable(screenName) {
    AppLogger.d(TAG, "초기화(최초 생성), time=${System.nanoTime()}")
    mutableStateOf(false)
}
```

이 로그 하나로 "Bundle 복원인지, 살아있는 객체 재사용인지"를 바로
구분할 수 있었다 (초기화 로그가 재생성 후 다시 찍히면 Bundle 복원,
안 찍히면 객체 재사용).

## 6. 참고 (검증에 사용한 공식 자료)

- `NavBackStackEntry` 레퍼런스: Lifecycle/ViewModelStore/SavedStateRegistry의
  생존 범위가 "백스택 위에 있는 동안"으로 명시됨.
- `NavBackStackEntry.LocalOwnersProvider` 레퍼런스: entry 자신을
  LocalSavedStateRegistryOwner 등으로 제공하고 SaveableStateHolder로
  콘텐츠 상태를 저장함이 명시됨.
- `ViewModelStore` 레퍼런스: configuration change 시 이전 인스턴스가
  새 owner에게 그대로 유지되어야 함이 명시됨 (entry도 동일 메커니즘 적용).
