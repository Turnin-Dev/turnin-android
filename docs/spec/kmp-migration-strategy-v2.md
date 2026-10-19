# Turnin Android → KMP 마이그레이션 전략 (v2)

- JIRA: PK-171
- 이전 버전: [kmp-migration-strategy-v1.md](kmp-migration-strategy-v1.md)

## 1. 방향

- **Android 네이티브를 유지**하면서, iOS와 함께 쓰게 된 기능만 하나씩 `:shared`로 옮긴다.
- 공유하지 않는 기능은 기존 Android 모듈(`:presentation`, `:domain`, `:data`, `:core:*`)에 그대로 둔다.
- 계층 경계(`data → domain ← presentation`)는 **모듈 의존성으로 빌드가 강제**한다. 실수로 domain에서 data를 import하면 컴파일 에러가 난다.
- 기능은 계층 모듈 안의 패키지(`feature/<기능>/`)로 나누고, 나중에 기능 단위 모듈로 쪼갤 수 있게 패키지 경계를 지킨다.
- 첫 공유 기능: **공지(Announcement)**. Android 공지 탭과 iOS 앱이 같은 UseCase·Repository·Ktor 구현을 사용한다.

## 2. V1과 다른 점

| 항목 | V1 | V2 | 이유 |
|---|---|---|---|
| 공유 단위 | 기존 모듈(`:domain`, `:core:domain`, `:core:data`)을 통째로 KMP 전환 | 공유할 기능만 `:shared:*`로 이동, 기존 모듈은 순수 JVM/Android 유지 | 공유하지 않는 기능까지 KMP 제약(`@Inject` 제거 64개, MockK → Mokkery 28개 파일, JVM API 대체)을 받는 비용이 컸다. Android 네이티브 개발 방식은 그대로 두고 공유 범위만 늘려 간다 |
| 공유 코드 위치 | `:core:data` commonMain에 Ktor·공지 구현, androidMain에 기존 코드 | `:shared:{domain,data,presentation}` | 한 모듈 안의 commonMain/androidMain 구분은 계층 경계를 강제하지 못한다. 계층별 모듈로 나눠 빌드가 막게 한다 |
| 의존 방향 | `:core:domain` 자체가 공유 모듈 | Android `:domain`, `:core:domain`은 `:shared`에 의존하지 않는다 | Android 도메인 계층이 공유 모듈 구현을 따라가지 않게 한다. 공유 기능을 쓰는 곳은 `:presentation`(UseCase·모델), `:core:data`(NetworkPolicy), `:app`(조립)으로 한정한다 |
| `:shared` 범위 | iOS 타겟만, macOS에서만 포함 | android + iOS 타겟, 항상 포함 (SKIE만 macOS에서 적용) | Android도 공유 기능을 사용하므로 Linux CI에서 Android 타겟을 빌드·테스트해야 한다 |
| iOS 진입점 | `SharedSdk` | `SharedEntryPoint` | "SDK"는 외부 라이브러리처럼 읽힌다. iOS가 공유 기능을 쓰려면 가장 먼저 거치는 진입점이라는 역할을 이름에 드러낸다 |
| 플랫폼 재료 한정자 | `PlatformQualifier` | `SharedQualifier` | 플랫폼별 코드가 아니라 공유 Koin 모듈이 플랫폼에 요구하는 재료를 구분하는 용도다 |
| Koin 브릿지 | `:core:data` androidMain의 `CommonDataKoinBridge` 클래스 | `:app`의 `SharedKoinBridge`(Hilt `@Provides`로 `Koin` 제공) | composition root는 `:app`이다. 기능별 `@Provides`가 `koin.get()`으로 꺼내므로 기능이 늘어도 브릿지는 바뀌지 않는다 |

## 3. 모듈 구조

```
shared/
├─ domain        core/(Result, NetworkErrorType) + feature/<기능>/(model, error, repository IF, usecase)   coroutines만 의존
├─ data          core/(HttpClient, NetworkPolicy, BearerAuth, TokenStorage, SharedQualifier)
│                + feature/<기능>/(dto, datasource, repository 구현, Koin data 모듈)                      → domain
├─ presentation  feature/<기능>/ iOS Facade·Swift용 UI 모델 (iosMain)                                      → domain
└─ (umbrella)    sharedModules(UseCase 등록), SharedEntryPoint·AuthFacade(iosMain), iOS 프레임워크 export   → 전부

Android:  :presentation → :shared:domain     :core:presentation → :shared:domain
          :core:data    → :shared:data       :app → :shared (umbrella, Koin → Hilt 브릿지)
iOS:      iosApp → Shared 프레임워크 (umbrella 하나, SKIE)
```

- 모든 `:shared:*` 모듈은 `turnin.kmp.library`(android + iosArm64 + iosSimulatorArm64)를 사용한다.
- 패키지: `com.turnin.shared.<계층>.core.*`, `com.turnin.shared.<계층>.feature.<기능>.*`
  - 기능끼리 import하지 않는다. 공통으로 필요한 코드는 `core`로 내린다.
  - `core`는 `feature`를 import하지 않는다.
  - 두 규칙은 `SharedPackageBoundaryTest`(umbrella `androidHostTest`)가 검사한다.
- 기능 단위 모듈로 나누는 방법과 시점은 [kmp-module-structure-comparison.md](../notes/kmp-module-structure-comparison.md)를 따른다.

## 4. 기술 결정

| 항목 | 결정 |
|---|---|
| 버전 | Kotlin 2.3.21, KSP 2.3.12, Hilt 2.58, AGP 8.13.2, Gradle 8.13 (근거는 V1 문서 4절과 같다) |
| 네트워크 | 공유 기능: **Ktor 3.5.2** + kotlinx.serialization. 엔진은 Android OkHttp, iOS Darwin. 공유하지 않는 Android 기능은 Retrofit/Moshi 유지 |
| 네트워크 정책 | 재시도 횟수·지연(지수 백오프 + Full Jitter)·재시도 대상 상태 코드/예외·타임아웃은 `:shared:data`의 `NetworkPolicy` 한 곳에서 정의한다. Android Retrofit(`retry()`, `networkCall`)도 같은 값을 사용한다 |
| 예외 분류 | 타임아웃·연결 실패 판정은 `NetworkExceptions`(expect/actual)로 Android(`java.net`)와 iOS(`NSURLErrorDomain`)를 같은 기준으로 판정한다 |
| DI | domain은 DI 프레임워크를 모른다(어노테이션 없는 생성자). 공유 객체는 Koin 모듈로 조립한다: data 모듈은 `:shared:data`의 기능 패키지, UseCase 등록은 umbrella의 기능 모듈. iOS는 `SharedEntryPoint`가, Android는 `:app`의 `SharedKoinBridge`가 Koin 컨테이너를 만든다 |
| Koin 컨테이너 | 플랫폼마다 **하나만** 만든다. `single` 인스턴스가 `Module` 객체에 보관되므로, 다시 만들 때는 이전 컨테이너를 먼저 닫는다 (테스트 포함). 전역 `startKoin`은 쓰지 않는다 |
| Koin → Hilt | `SharedKoinBridge`가 Hilt 재료(`@TokenOkHttpClient`, `@IO`)를 `SharedQualifier`로 등록하고 `Koin`을 Hilt에 제공한다. 기능별 Hilt 모듈(`SharedXxxModule`)이 `koin.get()`으로 UseCase 묶음을 꺼낸다. 재료는 Hilt → Koin, 결과물은 Koin → Hilt 한 방향으로만 흐른다 |
| 인증 | Android: Ktor OkHttp 엔진에 기존 `@TokenOkHttpClient`를 `preconfigured`로 넘겨 `TokenAuthenticator`(토큰 첨부·갱신·자동 로그아웃)를 Retrofit과 공유한다. iOS: Ktor `installBearerAuth` + `KeychainTokenStorage` (`AfterFirstUnlockThisDeviceOnly`, 설치 후 첫 실행 시 정리) |
| Result | Android `:core:domain`의 `Result`와 `:shared:domain`의 `Result`를 **둘 다 둔다**. 공유 기능을 쓰는 ViewModel은 `import ...shared.domain.core.result.Result as SharedResult`로 구분한다 (6절 추후 검토 참고) |
| 네트워크 에러 타입 | `NetworkErrorType`은 `:shared:domain`에 둔다. 요청이 "왜 실패했는지"의 의미만 표현하고 HTTP·Ktor 세부 사항은 모른다. 변환은 `:shared:data`가 한다. Android 화면은 `CommonErrorType`으로 1:1 변환해 기존 에러 문구를 쓴다 (`NetworkErrorTypeAsUiText.kt`) |
| iOS 연동 | 직접 연동(`embedAndSignAppleFrameworkForXcode`), 정적 umbrella 프레임워크 `Shared` 하나. `:shared:presentation`만 export한다 |
| Swift 연동 | **Facade + SKIE**. Facade(`AnnouncementFacade` 등)가 UseCase 결과를 제네릭 없는 sealed/enum·기본 타입(`AnnouncementUiState`, `UiAnnouncement`, `AnnouncementErrorReason`)으로 바꾸고, SKIE가 `Flow` → `AsyncSequence`, sealed/enum → Swift `enum`, suspend → `async`로 변환한다. SKIE 빌드 분석 수집은 끈다 |
| Facade 이름 | 호출하는 쪽(Swift)에서 본 역할이 "기능 창구"이므로 Facade로 한다. 타입 변환은 내부 구현이며, GoF Adapter가 전제하는 "클라이언트가 먼저 정한 인터페이스"가 없다 |
| `SharedEntryPoint` | `object`(전역 싱글톤)로 둔다. Swift가 Kotlin에 들어오는 유일한 진입점이기 때문이다 (아키텍처 규칙 예외, 승인됨). 기능 코드는 이 객체에 접근하지 않고 생성자 주입만 사용한다 |
| 테스트 | `commonTest`에서 `kotlin.test` + Mokkery + `kotlinx-coroutines-test` + Ktor `MockEngine`. Android 호스트(`androidHostTest`)와 iOS 시뮬레이터에서 모두 실행한다. Keychain은 Kotlin/Native 단위 테스트에서 쓸 수 없으므로(`errSecNotAvailable`) 앱에서 확인한다 |

## 5. 기능 이동 체크리스트

다음 기능을 `:shared`로 옮길 때 이 순서를 따른다. 패키지는 각 계층 모듈의 `feature/<기능>/`이다.

1. **domain** (`:shared:domain`): 모델, `XxxErrorType`, Repository 인터페이스, UseCase와 `XxxUseCases`. 생성자는 public, 어노테이션 없음. UseCase 테스트를 `commonTest`로 옮긴다.
2. **data** (`:shared:data`): DTO(kotlinx.serialization), DataSource(`ktorNetworkCall`), Repository 구현, `val xxxDataModule`(Koin). 인증이 필요한 요청은 `SharedQualifier.AuthHttpClient`를 쓴다.
3. **umbrella** (`:shared`): `xxxModule`(`includes(xxxDataModule)` + UseCase `factory`)을 만들고 `sharedModules`에 추가한다.
4. **Android 전환**
   - `:app/di/SharedXxxModule.kt`에서 `koin.get<XxxUseCases>()`를 `@Provides`한다.
   - ViewModel은 공유 UseCase 묶음을 주입받고 `SharedResult` alias로 결과를 처리한다. 에러 문구는 `presentation/<기능>/error/`에서 매핑한다.
   - 기존 Android 코드(Retrofit API, DTO, DataSource, Repository, UseCase, Hilt 등록, 테스트)를 삭제한다.
5. **iOS**: `:shared:presentation` iosMain에 Facade와 Swift용 상태·UI 모델을 만들고, umbrella `iosModules`에 Facade 모듈을 추가한다. Swift는 `SharedEntryPoint.inject()`로 꺼낸다.
6. **검증**: CI 세 단계(`ktlintCheck` → `testDebugUnitTest` → `assembleDebug`), `:shared:*:iosSimulatorArm64Test`, 프레임워크 링크, R8(`assembleReleaseTest`), 양쪽 앱 실행.

## 6. 추후 검토

- **Result 통합**: `:core:domain`이 `:shared:domain`에 의존하게 해 `Result`를 하나로 합치는 방법 (domain → domain 의존이라 계층 규칙은 지킨다). 공유 기능이 늘어 alias 사용이 번거로워지면 검토한다.
- **presentation 공유(CMP)**: ViewModel을 공유하면 `:shared:presentation` commonMain에 두고, 그때 Facade의 역할을 다시 정한다. 공유 ViewModel은 같은 `Result`를 쓰므로 위 통합과 함께 검토한다.
- **Android 토큰 저장소**: Android가 공유 인증(`installBearerAuth`)을 쓰게 되면 `DataStoreManager`를 감싸는 `TokenStorage` 구현을 추가한다.
- **`iosX64`**: Intel Mac 시뮬레이터가 필요해지면 타깃을 추가한다.
- **Kotlin 2.4 + AGP 9 + Hilt 2.60**: 변경 범위가 커서 별도 작업으로 진행한다.
- **기능 단위 모듈화**: 공유 기능이 3개 이상이 되거나 담당자가 나뉠 때 검토한다.

## 7. 참고 자료

- [Make your Android application work on iOS – 공식 튜토리얼](https://kotlinlang.org/docs/multiplatform/multiplatform-integrate-in-existing-app.html)
- [Multiplatform project configuration (umbrella 모듈)](https://kotlinlang.org/docs/multiplatform/multiplatform-project-configuration.html)
- [KMP 호환성 가이드](https://kotlinlang.org/docs/multiplatform/multiplatform-compatibility-guide.html)
- [Android-KMP 라이브러리 플러그인](https://developer.android.com/kotlin/multiplatform/plugin)
- [Ktor 멀티플랫폼 클라이언트](https://ktor.io/docs/client-create-multiplatform-application.html) / [Bearer 인증](https://ktor.io/docs/client-bearer-auth.html)
- [Koin – Kotlin Multiplatform](https://insert-koin.io/docs/reference/koin-mp/kmp)
- [SKIE](https://skie.touchlab.co/)
- [Mokkery](https://mokkery.dev/docs/Setup)
- [Bridging Two Worlds: KMP Dependencies with Koin in Android Native with Hilt – ProAndroidDev](https://proandroiddev.com/bridging-two-worlds-using-kmp-dependencies-with-koin-in-android-native-with-hilt-c1dc820cfccb)
- [Tivi](https://github.com/chrisbanes/tivi) / [Now in Android](https://github.com/android/nowinandroid) (모듈 구조 참고)
