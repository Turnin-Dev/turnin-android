# KMP 모듈 구조 비교: 실무 패턴 vs Turnin

Turnin의 `:shared` 구조를 실무에서 많이 쓰는 KMP 모듈 구조와 비교한다.
지금 구조를 이해하고, 나중에 기능 단위로 모듈화할 때 방향을 잡는 데 참고한다.

## 참고 자료

- JetBrains 공식 문서: [Multiplatform project configuration](https://kotlinlang.org/docs/multiplatform/multiplatform-project-configuration.html)
  - 단일 shared 모듈에서 시작하고, 코드가 커지면 여러 모듈 + **umbrella 모듈**로 나누도록 권장한다.
  - iOS에는 **프레임워크 하나만** 내보낸다. 모듈마다 프레임워크를 따로 만들면 공통 의존성(Kotlin 런타임, 코루틴 등)이 프레임워크마다 중복 포함되어 앱 크기가 커지고, 같은 의존성을 거쳐 전달한 상태가 서로 연결되지 않는다.
- [Tivi](https://github.com/chrisbanes/tivi) (Chris Banes): 기능 단위로 모듈화한 대표적인 KMP 앱
- [Now in Android](https://github.com/android/nowinandroid): Android 공식 샘플. KMP는 아니지만 `core:*` + `feature:*` 구조의 표준 예시로 많이 참고한다.

> 아래 실무 구조는 위 자료와 일반적인 패턴을 바탕으로 핵심만 단순화해 표현했다.

---

## 1. 계층 단위 모듈: 실무 패턴 vs Turnin 현재 구조

### 실무 패턴 (계층 단위 + umbrella)

```
shared/
├─ domain        모델, Repository 인터페이스, UseCase      (순수 Kotlin)
├─ data          Repository 구현, 네트워크(Ktor), DB(SQLDelight), DTO
├─ presentation  공유 ViewModel / 상태 (선택)
└─ (umbrella)    DI 조립, iOS 프레임워크 export
androidApp  → shared 모듈들 (Compose UI)
iosApp      → umbrella 프레임워크 하나 (SwiftUI)
```

- 기능은 각 계층 모듈 안에서 **패키지**로 나눈다.
- 계층 경계는 모듈 의존성(Gradle)이 강제한다. domain에서 data를 import하면 컴파일 에러가 난다.
- 보통 **새 프로젝트를 KMP로 시작**하므로 Android 앱도 모든 비즈니스 로직을 shared에서 가져온다.

### Turnin 현재 구조

```
shared/
├─ domain        core/(Result, 에러) + feature/<기능>/(model, repository IF, error, usecase)
├─ data          core/(HttpClient, NetworkPolicy, BearerAuth, PlatformQualifier) + feature/<기능>/(dto, datasource, repository 구현, Koin)
├─ presentation  feature/<기능>/ iOS Facade·Swift용 상태 모델 (추후 공유 ViewModel)
└─ (umbrella)    sharedModules(UseCase Koin), SharedEntryPoint, iOS 프레임워크 export

Android 네이티브 (유지)
├─ :presentation → :shared:domain           (공유 기능의 UseCase·모델만 사용)
├─ :core:data    → :shared:data             (NetworkPolicy·예외 분류 재사용)
├─ :core:domain  → :shared:domain           (Result 통합 시)
├─ :app          → :shared (umbrella)       (Koin → Hilt 브릿지)
└─ :domain, :data, :core:*                   공유하지 않는 기능은 기존 구조 그대로

iOS 네이티브 (SwiftUI)
└─ iosApp        → Shared 프레임워크 하나   (umbrella, SKIE)
                   공유 기능은 Facade로 사용, 공유하지 않는 기능은 Swift로 직접 개발
```

### 비교

| 항목 | 실무 패턴 (계층 단위) | Turnin 현재 구조 |
|---|---|---|
| 계층 분리 | 계층별 모듈 | 같음 (`domain`/`data`/`presentation` + umbrella) |
| 기능 분리 | 계층 모듈 안의 패키지 | 같음 (`feature/<기능>/`), 공통 코드는 `core/` |
| iOS 연동 | umbrella 프레임워크 하나 | 같음 (`Shared` 프레임워크, SKIE). iosApp은 공유 기능만 Facade로 쓰고 나머지는 Swift로 개발 |
| Android 앱 | 비즈니스 로직 전부 shared 사용 | **공유하기로 한 기능만** shared 사용, 나머지는 Android 네이티브 모듈 유지 |
| DI | Koin을 양쪽에서 직접 사용하는 경우가 많음 | shared는 Koin, Android는 Hilt 유지 (`:app`의 Koin 브릿지로 연결) |
| 전환 방식 | 처음부터 KMP | 기능 단위로 **점진적으로 이동** (iOS와 함께 쓰게 된 기능부터) |
| presentation 공유 | 공유 ViewModel을 두는 경우가 많음 | 지금은 iOS Facade만 둠, 공유 ViewModel은 CMP 도입 시 검토 |

**정리:** 모듈 모양은 실무 패턴과 같다. 다른 점은 Android 네이티브를 유지하면서 기능을 하나씩 옮긴다는 것이다.
그래서 Android 쪽에 Hilt 브릿지가 있고, 공유하지 않는 기능은 기존 모듈에 남아 있다.

---

## 2. 기능 단위 모듈: 실무 패턴 vs Turnin의 예상 구조

### 실무 패턴 (기능 단위 + umbrella, Tivi 형태)

```
core/*              공통 기반 (logging, preferences, analytics …)
api/*               외부 API 클라이언트 (예: api:trakt, api:tmdb)
data/models         공통 모델
data/<기능>         기능별 데이터 (예: data:episodes, data:shows, data:search)
domain              UseCase (Tivi는 하나로 둠)
ui/<기능>           기능별 화면 (예: ui:discover, ui:search, ui:show:details)
shared/*            umbrella: DI 조립, 플랫폼별 export
android-app, desktop-app, (iOS)
```

- data와 ui를 **기능별 모듈**로 나누고, umbrella가 전부 묶는다.
- 기능 모듈끼리는 서로 의존하지 않고, 공통 코드는 `core/*`, `data:models` 같은 하위 모듈에 둔다.
- 기능 하나를 고치면 그 기능 모듈만 다시 빌드하므로 빌드 속도가 빠르고, 팀별로 담당하기 좋다.

### Turnin의 예상 구조 (지금 구조에서 기능 단위로 나눈 뒤)

```
shared/
├─ core/
│  ├─ domain                  ← 지금 :shared:domain 의 core/
│  └─ data                    ← 지금 :shared:data 의 core/
├─ feature/
│  ├─ announcement/
│  │  ├─ domain               ← 지금 :shared:domain 의 feature/announcement/
│  │  ├─ data                 ← 지금 :shared:data 의 feature/announcement/
│  │  └─ presentation         ← 지금 :shared:presentation 의 feature/announcement/
│  └─ <다음 기능>/{domain, data, presentation}
└─ (umbrella)                 변경 없음: 조립 + iOS 프레임워크 export

Android 네이티브   :presentation → :shared:feature:<기능>:domain, :app → :shared (umbrella)
iOS 네이티브       iosApp → Shared 프레임워크 하나 (변경 없음)
```

옮기는 방법:

1. 각 계층 모듈에서 `feature/<기능>/` 폴더를 새 기능 모듈로 옮긴다. **패키지 이름은 그대로 둔다.** (Kotlin 패키지와 Gradle 모듈은 서로 독립)
2. `core/`는 `:shared:core:domain`, `:shared:core:data`로 옮긴다.
3. `:shared:data`의 `core/`에서 `internal`로 두었던 기능용 API(`ktorNetworkCall`, `NetworkResult`, `ApiPath` 등)를 public으로 바꾼다.
4. umbrella의 `sharedModules`와 Gradle 의존성을 고친다. Android는 의존성 한 줄만 바뀌고 import는 그대로이며, Swift 코드는 바뀌지 않는다.

### 비교

| 항목 | 실무 패턴 (기능 단위) | Turnin 예상 구조 |
|---|---|---|
| 기능 분리 | 기능별 모듈 | 같음 (`:shared:feature:<기능>:*`) |
| 기능 안의 계층 | 프로젝트마다 다름 (Tivi: data·ui만 기능별, domain은 하나) | domain·data·presentation을 모두 기능별로 나눔 (지금 계층 구조를 그대로 유지) |
| 공통 코드 | `core/*`, `data:models` 등 | `:shared:core:domain`, `:shared:core:data` |
| 기능 간 의존 | 금지 (공통 모듈을 거침) | 금지. 지금부터 `feature/` 패키지 간 import를 테스트로 검사 |
| iOS 연동 | umbrella 하나 | 같음 |
| 전환 비용 | 처음부터 기능 단위로 설계 | 폴더 이동 + build.gradle 추가 + `core` 가시성 일부 수정 |

**정리:** 지금 구조는 패키지(`core/`, `feature/<기능>/`)를 미래 모듈 경계에 맞춰 두었다.
그래서 기능 단위로 나누는 작업이 대부분 기계적이다. 어렵게 만드는 요인은 기능 간 참조뿐이고, 이건 import 검사 테스트로 미리 막는다.

---

## 언제 기능 단위로 나눌까

지금처럼 공유 기능이 적을 때는 계층 단위만으로 충분하다. 아래 중 하나에 해당하면 기능 단위 모듈화를 검토한다.

- 공유 기능이 3개 이상으로 늘어 `:shared:*` 빌드 시간이 눈에 띄게 길어졌을 때
- 기능별로 담당자(팀)가 나뉘었을 때
- 특정 기능만 다른 앱이나 모듈에서 따로 쓰고 싶을 때
