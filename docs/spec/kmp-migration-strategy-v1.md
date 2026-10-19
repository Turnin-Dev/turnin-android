# Turnin Android → KMP 마이그레이션 전략

> V1 기록용 문서다. 현재 기준은 [kmp-migration-strategy-v2.md](kmp-migration-strategy-v2.md)를 따른다.

- JIRA: PK-171
- 최종 수정: 2026-10-10

## 1. 방향

- 순서: **현재 → KMP 적용 → 기능 단위 모듈화(추후 예정)**
- 공유 범위: **domain** + **공지 API 1개(data, 실험)** (UI·ViewModel은 추후 CMP 사용 예정, 이번 범위 제외)
- data: **Android 앱 전용으로 유지**, 공지 API만 실험적으로 Retrofit → **Ktor**로 전환해 `:core:data` commonMain으로 이동 (
  Android·iOS 공용)
- 모듈 구조: 기존 레이어 모듈 유지, 기능별 패키지 경계 유지
- 목표: 2~3일 내 iOS에서 shared UseCase 호출까지 확인

## 2. 현황 (2026-10-10 코드 기준)

| 모듈                                                          | 타입                | 상태                                                                                                            |
|-------------------------------------------------------------|-------------------|---------------------------------------------------------------------------------------------------------------|
| `:core:domain`                                              | 순수 JVM            | KMP 전환 대상 (1순위)                                                                                               |
| `:domain`                                                   | 순수 JVM            | KMP 전환 대상                                                                                                     |
| `:core:data`                                                | Android           | KMP 전환 대상. 기존 코드(Retrofit, Moshi, Room, DataStore, Hilt)는 androidMain 유지, commonMain에는 Ktor 네트워크 인프라 + 공지 API |
| `:data`                                                     | -                 | 삭제 (기능 단위 모듈화 시 부활 예정)                                                                                        |
| `:presentation`, `:core:presentation`, `:core:designsystem` | Android + Compose | 이번 범위 제외                                                                                                      |
| `:core:common`                                              | Android           | 미정 (Timber, Firebase 의존)                                                                                      |

### domain 전환 시 걸리는 부분

| 항목             | 규모                                                                          | 처리                                                                    |
|----------------|-----------------------------------------------------------------------------|-----------------------------------------------------------------------|
| `javax.inject` | `@Inject` 사용 클래스 64개                                                        | 제거 후 Android는 Hilt `@Provides`로 등록 (3. 기술 결정 참고)                      |
| MockK 테스트      | 28개 파일                                                                      | **Mokkery** + `kotlin.test`로 바꿔 `commonTest`로 이동 (3. 기술 결정 테스트 항목 참고) |
| JVM 전용 API     | `FileNameGenerator` (`java.time`, `UUID`), `this::class.java.simpleName` 6곳 | `kotlin.time` / `kotlin.uuid`(또는 kotlinx-datetime), TAG 상수로 대체        |
| `PagingData`   | `paging-common` 3.3.6만 사용                                                   | paging-common은 3.3.0부터 iOS 타겟 지원 → **commonMain에 그대로 유지**             |
| 로깅             | `DomainLogger` 인터페이스 존재                                                     | **그대로 사용**, 구현은 플랫폼별 (Android: Timber)                                |

### 공지 API 전환 시 걸리는 부분

- 인증: `TokenInterceptor` / `TokenAuthenticator`(Mutex·더블 체크 토큰 갱신, 실패 시 자동 로그아웃)가 OkHttp에 묶여 있음
    - Android: Ktor **OkHttp 엔진에 기존 `@TokenOkHttpClient`를 `preconfigured`로 전달**해 토큰 첨부·갱신·로그아웃, 캐시,
      로그 마스킹을 그대로 사용한다.
      Retrofit과 Ktor가 같은 `TokenAuthenticator`(Mutex)를 공유하므로 토큰 갱신이 중복 실행되지 않는다.
      (Ktor `Auth`를 따로 두면 두 스택이 각각 갱신해 refresh token 교체 방식에서 강제 로그아웃 위험이 있다.)
    - iOS: Darwin 엔진 + Ktor `Auth`(`installBearerAuth`, `:core:data` commonMain — Android
      `TokenAuthenticator`와 같은 규칙), 토큰 저장은 Keychain (4단계, `:shared`
      iosMain)
    - commonMain 공지 코드는 인증을 알지 못한다 (플랫폼이 넘겨주는 `HttpClient`에 포함).
- 에러 처리: `networkCall`이 Retrofit `Response`·Moshi·`java.net` 예외에 의존 → Ktor용 `ktorNetworkCall` 별도
  구현 (
  `NetworkResult` / `NetworkErrorType` 매핑은 유지)
- 정책 일원화: 재시도(횟수·지연·재시도 대상 상태 코드/예외)와 타임아웃은 commonMain `NetworkPolicy` 한 곳에서 정의하고
  Retrofit·Ktor가 함께 사용한다. 에러 응답 파싱·예외 메시지 마스킹도 공용 함수로 통일한다. 예외 분류(타임아웃·호스트 조회 실패·연결 실패)는
  expect/actual로 Android(`java.net`)와 iOS(`NSURLErrorDomain` 코드)를 같은 기준으로 판정한다.
- 재시도 대기: **모든 재시도 전에 대기**한다 (지수 백오프 + Full Jitter). 기존 Retrofit `retry()`는 마지막 재시도 직전에 대기하지 않아
  설계 문서(`RETRY_NETWORK_CALL_FLOW.md`)와 달랐으므로 통일하면서 수정한다.

## 3. 기술 결정

| 항목                                    | 결정                                                                                                                                                                                                                                                                                              |
|---------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 버전                                    | **Kotlin 2.3.21 / KSP 2.3.12 / Hilt 2.58**, AGP 8.13.2·Gradle 8.13 유지 (4. 버전 결정 근거 참고)                                                                                                                                                                                                          |
| 네트워크                                  | Android data: **Retrofit/OkHttp 유지**. 공지 API만 Retrofit → **Ktor Client** (직접 호출 방식). Ktor 엔진은 Android **OkHttp**(기존 토큰 OkHttpClient 재사용), iOS **Darwin**                                                                                                                                        |
| 직렬화                                   | Android data: Moshi 유지. 공지 API DTO만 Moshi → **kotlinx.serialization**                                                                                                                                                                                                                           |
| DI                                    | **shared 코드는 DI 비의존(어노테이션 없는 일반 생성자)**. Android는 **Hilt 유지**, UseCase는 `:app/di/<feature>/`에서 `@Provides`로 등록 (기능 모듈화 시 각 `:feature:x`로 이동). 공유 data 객체(공지 Ktor 구현 등)는 commonMain **Koin** 모듈에서 정의하고, iOS는 Koin으로 직접, Android는 **Koin → Hilt 브릿지**로 받는다 (아래 DI 결정 배경 참고)                        |
| domain 타겟                             | **android(`com.android.kotlin.multiplatform.library`) + iosArm64 + iosSimulatorArm64**                                                                                                                                                                                                          |
| Android 타겟 플러그인                       | `com.android.kotlin.multiplatform.library` (AGP 9부터 KMP와 `com.android.library` 병용 불가 → 지금부터 적용해 AGP 9 전환 시 재작업 방지). build type 미지원                                                                                                                                                              |
| 로컬 DB / 설정                            | Android data에서 유지 (shared 이관 없음)                                                                                                                                                                                                                                                                |
| 플랫폼 SDK (Firebase, Kakao, Google 로그인) | shared에는 인터페이스만, 구현은 각 플랫폼                                                                                                                                                                                                                                                                      |
| 로깅                                    | 기존 `DomainLogger` 인터페이스 사용 (Kermit 미도입)                                                                                                                                                                                                                                                         |
| Paging                                | `paging-common` KMP 지원 버전 사용, commonMain 유지                                                                                                                                                                                                                                                     |
| 테스트                                   | KMP 모듈 테스트는 `commonTest`에서 **Mokkery** + `kotlin.test` + `kotlinx-coroutines-test`로 작성 (Android 호스트·iOS 양쪽 실행). Mokkery는 Kotlin 컴파일러 플러그인이므로 Kotlin 버전업 시 함께 올린다 (Kotlin 2.3.21 → Mokkery 3.3.0)                                                                                                |
| iOS 연동                                | 공식 튜토리얼의 **직접 연동** (`embedAndSignAppleFrameworkForXcode`). umbrella `:shared`(iOS 타겟만, 정적 프레임워크 `Shared`)가 iOS 진입점과 Swift용 Facade를 담당한다. `SharedSdk`는 생명주기·DI 컨테이너(별도 Koin 컨텍스트)만 맡고, 기능 API는 기능별 Facade(`AuthFacade`, `AnnouncementFacade`)가 제공하며 `featureModules`에 등록한다. Swift는 `SharedSdk.inject()`로 Facade를 꺼낸다 (기능 추가 시 `SharedSdk` 변경 없음). 도메인 모듈은 export하지 않고 Facade가 Swift용 타입으로 변환한다. `:shared`는 macOS에서만 `settings`에 포함한다 (Linux CI 영향 차단) |
| iOS 검증                                | SwiftUI 최소 화면 1개(`iosApp/`)에서 공지 UseCase 호출. 로그인 전까지 DEBUG 빌드의 개발용 토큰 입력으로 인증                                                                                                                                                                                                                   |
| Swift 연동                              | **Facade + SKIE**: Facade가 UseCase 결과를 제네릭 없는 sealed/enum·기본 타입으로 변환하고, SKIE가 `Flow`→`AsyncSequence`, sealed/enum→Swift `enum`, suspend→`async`로 변환한다. SKIE 빌드 분석 업로드는 끈다 (`skie { analytics { disableUpload } }`). SKIE는 컴파일러 플러그인이므로 Kotlin 버전업 시 지원 범위를 확인한다                                 |
| 인증 토큰 저장                              | 공통 `TokenStorage` 인터페이스(`:core:data` commonMain)는 공유 인증 로직(`installBearerAuth`)이 사용하며, 현재 **iOS만 구현**한다: **Keychain 직접 저장** (`AfterFirstUnlockThisDeviceOnly`, 설치 후 첫 실행 시 정리). Android는 기존 `DataStoreManager`(Keystore 암호화) + OkHttp `TokenAuthenticator`를 유지하고, 공유 인증 로직을 쓰게 되면 `DataStoreManager`를 감싸는 구현을 추가한다. iOS에서 AES 키는 앱 메모리로 꺼내지므로 "Keychain 키 + DataStore 암호문"은 Keychain 직접 저장보다 보호 수준이 높지 않다 |
| 기능 모듈화 대비                             | 기능별 패키지 유지, 기능 간 내부 import 금지, Koin 모듈은 기능별 분리                                                                                                                                                                                                                                                  |
| 공지 배치                                 | 모델·Repository 인터페이스는 `:core:domain` 유지, Ktor 구현체·Koin 모듈은 `:core:data` commonMain, Koin 브릿지·Hilt 바인딩은 `:core:data` androidMain (기능 모듈화 시 인터페이스는 `:domain`, 구현·바인딩은 `:data`로 이동)                                                                                                                 |
| `:core:data` 플러그인                     | **`kotlin.multiplatform` + `com.android.library`** (`androidTarget` + iosArm64 + iosSimulatorArm64). build type·`BuildConfig`·Hilt Gradle Plugin을 그대로 쓰기 위함. 전용 convention plugin으로 구성 (아래 스파이크 결과 참고)                                                                                          |

### `:core:data` KMP 전환 스파이크 결과

`kotlin.multiplatform` + `com.android.library` 조합으로 빌드 파일만 바꿔 확인했다 (소스 위치 변경 없음).

| 항목                             | 결과                                                  |
|--------------------------------|-----------------------------------------------------|
| Hilt (KSP, Hilt Gradle Plugin) | 통과 (`ksp` → `kspAndroid` 설정 이름만 변경)                 |
| Room (KSP, 스키마 export)         | 통과                                                  |
| 단위 테스트 (Robolectric 포함)        | 329개 통과                                             |
| `BuildConfig` / build type     | `:app:assembleDebug`, `:app:assembleReleaseTest` 통과 |
| iOS 타겟 (commonMain)            | `iosArm64`, `iosSimulatorArm64` 컴파일 통과              |

- Android-KMP 라이브러리 플러그인은 build type·`BuildConfig` 미지원이라 제외했다. `:core:data`는 build type별
  `buildConfigField` 5개와 `BuildConfig.DEBUG`(6개 파일)를 사용한다.
- 참고: 이 조합은 AGP 9와 호환되지 않는다. 당분간 AGP 8.13을 유지하므로 고려하지 않는다.

### DI 결정 배경

- 전체를 Koin으로 통일하는 편이 KMP 관점에서는 효율적이지만, Android 네이티브 실무에서 Hilt 비중이 높아 Android는 Hilt를 유지한다.
- shared 코드가 DI 프레임워크에 의존하지 않으므로 공유 범위(UseCase·Repository)는 줄어들지 않는다. DI는 플랫폼별 composition root에서만
  다룬다.
- 비용: `@Inject`를 제거한 클래스(64개)를 Hilt `@Provides`로 등록해야 한다.
- UseCase `@Provides` 위치는 composition root인 `:app`으로 한다 (Clean Architecture의 Main 컴포넌트, Android 모듈화
  가이드의
  "app module provides actual implementation").
    - `:core:data`: `:domain`에 의존해야 하므로 core → 기능 계층 역방향 의존이 생긴다.
    - `:presentation`: 의존 방향은 맞지만 UI 계층이 객체 조립 책임까지 갖게 된다.
    - Repository `@Binds`는 구현체를 가진 `:core:data`에 둔다 (구현 소유 모듈이 바인딩).
- `javax.inject`는 JVM 전용이라 commonMain에서 사용할 수 없다. expect/actual + typealias로 `@Inject`를 유지하는 방법은
  Hilt(KSP2) 호환이 검증되지 않아 채택하지 않는다.

### Koin → Hilt 브릿지

공유 data 객체의 조립 방법(Ktor 클라이언트, 인증, Repository)은 commonMain Koin 모듈 한 곳에서 정의하고, Android는 브릿지로 받아 Hilt
그래프에 넣는다.

```kotlin
// :core:data androidMain — Hilt가 생성·소유하는 별도 Koin 컨텍스트
// 앱에 하나만 존재해야 한다 (인스턴스마다 Koin 컨테이너가 생성됨). 재료가 늘면 provider 인자와 PlatformQualifier를 추가한다.
class CommonDataKoinBridge(platformModule: KoinModule) {
    private val koin: Koin = koinApplication { modules(platformModule, commonDataModule) }.koin

    fun announcementRepository(): AnnouncementRepository = koin.get()
}

@Module
@InstallIn(SingletonComponent::class)
object CommonDataBridgeModule {
    @Provides
    @Singleton
    fun provideCommonDataKoinBridge(
        @TokenOkHttpClient tokenOkHttpClient: OkHttpClient,
        @IO ioDispatcher: CoroutineDispatcher,
    ): CommonDataKoinBridge = CommonDataKoinBridge(
        platformModule = module {
            // Koin은 반환 타입으로 조회하므로 타입을 명시한다.
            single<HttpClient>(PlatformQualifier.AuthHttpClient) {
                createHttpClient(OkHttp.create { preconfigured = tokenOkHttpClient }, BuildConfig.TURNIN_SERVER_URL)
            }
            single<CoroutineDispatcher>(PlatformQualifier.IoDispatcher) { ioDispatcher }
        },
    )

    @Provides
    @Singleton
    fun provideAnnouncementRepository(commonDataKoinBridge: CommonDataKoinBridge): AnnouncementRepository =
        commonDataKoinBridge.announcementRepository()
}
```

- 참고 자료의 두 방식을 비교했다.
    - jacobras/KMP-shared-DI: 공유 모듈 androidMain의 Hilt 모듈에서 `KoinJavaComponent.get(Class)`,
      `Application.onCreate`에서 전역 `startKoin`.
    - ProAndroidDev(Bridging Two Worlds): androidMain의 `object KoinBridge : KoinComponent`에서 기능별 이름의
      함수 + reified `get<T>()`, Hilt `@Provides @Singleton`.
    - 런타임 비용은 같다 (Hilt가 한 번 요청할 때 Koin 조회 1회, 이후 캐시).
- ProAndroidDev 방식(기능별 이름의 브릿지 함수, Hilt `@Provides @Singleton`)을 따르되 **별도 Koin 컨텍스트(
  `koinApplication`)**를 사용한다.
    - 공유 Koin 모듈은 플랫폼 재료(OkHttpClient, 서버 URL)가 필요하므로 `object` 대신 **Hilt가 생성·소유하는 클래스**로 둔다.
      재료는 Hilt → Koin, 결과물은 Koin → Hilt 한 방향씩만 흐른다.
    - 전역 `startKoin` 호출 순서에 의존하지 않는다 (Hilt 주입이 Koin 시작보다 먼저 일어나도 안전).
    - 다른 Koin 사용처와 전역 컨텍스트가 충돌하지 않는다.
- 브릿지 범위는 Koin이 생성하는 공유 data 객체로 한정한다. UseCase는 Retrofit/Room 기반 Repository(Hilt)에 의존하므로 `:app`
  `@Provides`를 유지한다.
- Koin 정의 누락은 런타임에만 드러나므로 Koin 모듈 검증 테스트(`verify()`)를 둔다.
- ViewModel 공유(CMP) 단계에서는 Hilt를 commonMain에서 쓸 수 없으므로 공유 범위(ViewModel → UseCase → Repository)의 DI를
  KMP DI(Koin 등)로 옮긴다. UseCase가 DI 비의존 생성자라 Koin 모듈 작성만으로 전환할 수 있다.

### 테스트 결정 배경

| 방식                           | iOS 실행 | 현재 환경 (AGP 8.13, Kotlin 2.3.21)    | 비고                                               |
|------------------------------|--------|------------------------------------|--------------------------------------------------|
| MockK 유지 (`androidHostTest`) | X      | 가능                                 | 공유 코드의 iOS 동작을 검증하지 못함                           |
| MocKMP                       | O      | **불가** (3.x는 AGP 8 비호환)            | KSP 생성, 인터페이스·추상 클래스만 mock                       |
| **Mokkery**                  | O      | 가능 (3.3.0이 Kotlin 2.3.0–2.3.21 지원) | 컴파일러 플러그인, MockK와 유사한 문법. final 클래스는 all-open 필요 |

- MockK → Mokkery 변환: `mockk<T>()` → `mock<T>()`, `coEvery` → `everySuspend`, `coVerify` →
  `verifySuspend`, `just Runs` → `returns Unit`, JUnit → `kotlin.test`.
- final 클래스(UseCase)를 mock하던 테스트는 실제 UseCase를 조립하고 Repository를 mock하는 방식으로 바꾼다.

## 4. 버전 결정 근거

- Kotlin 2.1.10이 공식 지원하는 Xcode는 16.0 → Xcode 26 사용을 위해 Kotlin 버전업이 필요하다.
- Hilt Gradle Plugin은 **2.59부터 AGP 9 필수** → AGP 8.13 유지 시 Hilt 상한은 **2.58**.
- Hilt 2.58의 kotlin-metadata-jvm은 Kotlin 2.3 메타데이터까지 읽을 수 있다 (N+1 허용). Kotlin 2.4는 Hilt 2.60(→ AGP
  9)이 필요하다.
- KSP 2.3.x부터 KSP1이 제거되고 버전이 Kotlin과 분리됨 (`2.3.12` 형식). KSP 사용처는 Hilt·Room뿐이며 Room 2.7.2는 KSP2 지원.
- 결론: **Kotlin 2.3.21 (Xcode 26.0 공식 지원) + KSP 2.3.12 + Hilt 2.58**, AGP/Gradle 유지.
- `build-logic`은 Gradle 8.13에 들어 있는 Kotlin(2.0)으로 컴파일되므로, Kotlin 2.3으로 빌드된 플러그인 jar(KSP 2.3.x 등)는
  `compileOnly`로 둘 수 없다. 사용하지 않던 `ksp-gradlePlugin` 의존성은 제거했다.
- 설치된 Xcode가 26.0보다 높으면 Kotlin/Native 호환성 경고가 나올 수 있으나 보통 동작한다.
- **Ktor 3.5.2** (최신 3.6.0 아님): Ktor 3.6.0의 OkHttp 엔진은 OkHttp 5.5.0을 사용하고, OkHttp 5.4.0부터 compileSdk
  36, 5.5.0부터 37을
  요구한다 (AAR `minCompileSdk`). AGP 8.13의 최대 compileSdk는 36이므로 OkHttp 5.3.2를 쓰는 Ktor 3.5.2로 고정한다.
- KMP 라이브러리는 프로젝트 Kotlin(2.3.21) 이하로 빌드된 버전을 쓴다 (Kotlin/Native klib는 상위 버전 호환이 보장되지 않음).
  Ktor 3.5.2(2.3.21), Koin 4.2.2(2.3.20), kotlinx-serialization 1.11.0(2.3.20).
- **Kotlin 2.4 + Hilt 2.60 + AGP 9** (Xcode 26.4 공식 지원)는 `kotlin.android` 제거, Gradle 9.1+ 등 변경이 커서
  5단계 이후로 미룬다.

## 5. 미정

- `:core:common`, 소셜 로그인, 암호화(`crypto`) 처리
- 파일 업로드(Multipart) 이관 방식

## 6. 단계별 계획

| 단계                            | 내용                                                                                                                                                                                                             |
|-------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 0. 준비                         | Kotlin/KSP/Hilt 버전업, KMP convention plugin 추가, version catalog 정리, `:core:data` KMP 전환 스파이크                                                                                                                    |
| 1. `:core:domain` + `:domain` | 두 모듈을 함께 KMP 전환 (android + iOS 타겟, commonMain). JVM 전용 API 대체, 테스트를 Mokkery로 바꿔 `commonTest`로 이동, `@Inject` 제거 후 Hilt `@Provides`로 등록. `:domain`(JVM)이 KMP 전환된 `:core:domain`(android 타겟)을 소비할 수 없으므로 분리하지 않는다 |
| 2. `:core:data`               | KMP 전환 (기존 코드는 전부 androidMain 유지). Android 빌드·테스트가 그대로 통과하는지 확인                                                                                                                                                |
| 3. 공지 API 전환 (실험)             | **공지(Announcement)** API를 Retrofit → Ktor + serialization으로 전환, `:core:data` commonMain으로 이동 (인증·에러 처리 포함)                                                                                                     |
| 4. DI + iOS 검증                | `:shared` 구성(iOS 진입점, Keychain, Facade + SKIE), commonMain bearer 인증, Android는 Koin → Hilt 브릿지 연결, iOS에서 공지 UseCase 호출                                                                                         |
| 5 이후                          | Kotlin 2.4 + AGP 9 전환, 나머지 data 공유 여부 결정, presentation 공유(CMP) 결정 (ViewModel 공유 시 공유 계층 DI를 KMP DI로 전환), 기능 단위 모듈화                                                                                             |

## 7. 참고 자료

### 공식 문서

- [Make your Android application work on iOS – 공식 튜토리얼](https://kotlinlang.org/docs/multiplatform/multiplatform-integrate-in-existing-app.html)
- [KMP 호환성 가이드 (Kotlin–Xcode–AGP–Gradle 호환표)](https://kotlinlang.org/docs/multiplatform/multiplatform-compatibility-guide.html)
- [AGP 9 / Android-KMP 라이브러리 플러그인 전환](https://kotlinlang.org/docs/multiplatform/multiplatform-project-agp-9-migration.html)
- [Android-KMP 라이브러리 플러그인 (Android Developers)](https://developer.android.com/kotlin/multiplatform/plugin)
- [iOS 연동 방식 개요 (직접 연동 / SwiftPM / CocoaPods)](https://kotlinlang.org/docs/multiplatform/multiplatform-ios-integration-overview.html)
- [Ktor 멀티플랫폼 클라이언트](https://ktor.io/docs/client-create-multiplatform-application.html)
- [Ktor Bearer 인증 (토큰 갱신)](https://ktor.io/docs/client-bearer-auth.html)
- [Koin – Kotlin Multiplatform](https://insert-koin.io/docs/reference/koin-mp/kmp)
- [Paging 릴리스 노트 (KMP 지원)](https://developer.android.com/jetpack/androidx/releases/paging)

### 버전 호환성

- [Dagger 릴리스 노트 (2.58 / 2.59 AGP 9 요구)](https://github.com/google/dagger/releases)
- [Dagger #5001 – Kotlin 2.3 메타데이터 지원](https://github.com/google/dagger/issues/5001)
- [KSP 릴리스 노트](https://github.com/google/ksp/releases)

### 사례 / 레포

- [Convert Your Native Project to KMP – Droids On Roids](https://proandroiddev.com/convert-your-native-project-to-kotlin-multiplatform-developers-guide-bce1ae20bd32) (
  umbrella 모듈 포함)
- [Touchlab KaMPKit](https://github.com/touchlab/KaMPKit) (Ktor + Koin + iOS 연동 구조)
- [jacobras/KMP-shared-DI](https://github.com/jacobras/KMP-shared-DI) (Koin + Hilt + Swinject 혼용)
- [Bridging Two Worlds: KMP Dependencies with Koin in Android Native with Hilt – ProAndroidDev](https://proandroiddev.com/bridging-two-worlds-using-kmp-dependencies-with-koin-in-android-native-with-hilt-c1dc820cfccb)

### 테스트

- [Mokkery – Setup (Kotlin 버전 호환표)](https://mokkery.dev/docs/Setup) / [Limitations](https://mokkery.dev/docs/Limitations)
- [MocKMP](https://github.com/kosi-libs/MocKMP)
