# Turnin Android → KMP 마이그레이션 전략

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

| 모듈                                                          | 타입                | 상태                                                                                                           |
|-------------------------------------------------------------|-------------------|--------------------------------------------------------------------------------------------------------------|
| `:core:domain`                                              | 순수 JVM            | KMP 전환 대상 (1순위)                                                                                              |
| `:domain`                                                   | 순수 JVM            | KMP 전환 대상                                                                                                    |
| `:core:data`                                                | Android           | KMP 전환 대상. 기존 코드(Retrofit, Moshi, Room, DataStore, Hilt)는 androidMain 유지, commonMain에는 Ktor 네트워크 인프라 + 공지 API |
| `:data`                                                     | -                 | 삭제 (기능 단위 모듈화 시 부활 예정)                                                                                       |
| `:presentation`, `:core:presentation`, `:core:designsystem` | Android + Compose | 이번 범위 제외                                                                                                     |
| `:core:common`                                              | Android           | 미정 (Timber, Firebase 의존)                                                                                     |

### domain 전환 시 걸리는 부분

| 항목             | 규모                                                                          | 처리                                                             |
|----------------|-----------------------------------------------------------------------------|----------------------------------------------------------------|
| `javax.inject` | `@Inject` 사용 클래스 64개                                                        | 제거 후 Android는 Hilt `@Provides`로 등록 (3. 기술 결정 참고)               |
| MockK 테스트      | 28개 파일                                                                      | 우선 `androidHostTest`로 이동해 그대로 유지, `commonTest` Fake 전환은 점진적으로  |
| JVM 전용 API     | `FileNameGenerator` (`java.time`, `UUID`), `this::class.java.simpleName` 6곳 | `kotlin.time` / `kotlin.uuid`(또는 kotlinx-datetime), TAG 상수로 대체 |
| `PagingData`   | `paging-common` 3.3.6만 사용                                                   | paging-common은 3.3.0부터 iOS 타겟 지원 → **commonMain에 그대로 유지**      |
| 로깅             | `DomainLogger` 인터페이스 존재                                                     | **그대로 사용**, 구현은 플랫폼별 (Android: Timber)                         |

### 공지 API 전환 시 걸리는 부분

- 인증: `TokenInterceptor` / `TokenAuthenticator`(토큰 갱신)가 OkHttp에 묶여 있음 → Ktor `Auth` 플러그인(bearer +
  `refreshTokens`) + 토큰 저장소 인터페이스(`TokenProvider` 등) 필요 (인터페이스는 `:core:data` commonMain, 구현은
  Android: androidMain / iOS: `:shared` iosMain)
- 에러 처리: `networkCall`이 Retrofit `Response`·Moshi·`java.net` 예외에 의존 → Ktor용 `networkCall` 별도 구현 (
  `NetworkResult` / `NetworkErrorType` 매핑은 유지)

## 3. 기술 결정

| 항목                                    | 결정                                                                                                                                                                                   |
|---------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 버전                                    | **Kotlin 2.3.21 / KSP 2.3.12 / Hilt 2.58**, AGP 8.13.2·Gradle 8.13 유지 (4. 버전 결정 근거 참고)                                                                                               |
| 네트워크                                  | Android data: **Retrofit/OkHttp 유지**. 공지 API만 Retrofit → **Ktor Client** (직접 호출 방식)                                                                                                  |
| 직렬화                                   | Android data: Moshi 유지. 공지 API DTO만 Moshi → **kotlinx.serialization**                                                                                                                |
| DI                                    | **shared 코드는 DI 비의존(어노테이션 없는 일반 생성자)**. Android는 **Hilt 유지**, UseCase는 `:app/di/<feature>/`에서 `@Provides`로 등록 (기능 모듈화 시 각 `:feature:x`로 이동). iOS 진입점에서만 **Koin** 사용 (공지 기능 한정) |
| domain 타겟                             | **android(`com.android.kotlin.multiplatform.library`) + iosArm64 + iosSimulatorArm64**                                                                                               |
| Android 타겟 플러그인                       | `com.android.kotlin.multiplatform.library` (AGP 9부터 KMP와 `com.android.library` 병용 불가 → 지금부터 적용해 AGP 9 전환 시 재작업 방지). build type 미지원                                                   |
| 로컬 DB / 설정                            | Android data에서 유지 (shared 이관 없음)                                                                                                                                                     |
| 플랫폼 SDK (Firebase, Kakao, Google 로그인) | shared에는 인터페이스만, 구현은 각 플랫폼                                                                                                                                                           |
| 로깅                                    | 기존 `DomainLogger` 인터페이스 사용 (Kermit 미도입)                                                                                                                                              |
| Paging                                | `paging-common` KMP 지원 버전 사용, commonMain 유지                                                                                                                                          |
| 테스트                                   | 신규 commonTest는 Fake + kotlin.test (MockK 미사용). 기존 MockK 테스트는 `androidHostTest`로 이동 후 점진 전환                                                                                           |
| iOS 연동                                | 공식 튜토리얼의 **직접 연동** (`embedAndSignAppleFrameworkForXcode`). KMP 모듈이 3개(`:core:domain`, `:domain`, `:core:data`)이므로 umbrella `:shared` 도입 (코드 없이 export + iOS 진입점(Koin, Keychain)만 담당) |
| iOS 검증                                | Xcode 26 설치 후, SwiftUI 최소 화면 1개에서 공지 UseCase 호출                                                                                                                                      |
| 기능 모듈화 대비                             | 기능별 패키지 유지, 기능 간 내부 import 금지, Koin 모듈은 기능별 분리                                                                                                                                       |
| 공지 배치                                 | 모델·Repository 인터페이스는 `:core:domain` 유지, Ktor 구현체는 `:core:data` commonMain, Hilt 바인딩은 `:core:data` androidMain (기능 모듈화 시 인터페이스는 `:domain`, 구현·바인딩은 `:data`로 이동)                       |
| `:core:data` 플러그인                     | **`kotlin.multiplatform` + `com.android.library`** (`androidTarget` + iosArm64 + iosSimulatorArm64). build type·`BuildConfig`·Hilt Gradle Plugin을 그대로 쓰기 위함. 전용 convention plugin으로 구성 (아래 스파이크 결과 참고)        |

### `:core:data` KMP 전환 스파이크 결과

`kotlin.multiplatform` + `com.android.library` 조합으로 빌드 파일만 바꿔 확인했다 (소스 위치 변경 없음).

| 항목                                   | 결과                                                        |
|--------------------------------------|-----------------------------------------------------------|
| Hilt (KSP, Hilt Gradle Plugin)       | 통과 (`ksp` → `kspAndroid` 설정 이름만 변경)                        |
| Room (KSP, 스키마 export)               | 통과                                                        |
| 단위 테스트 (Robolectric 포함)              | 329개 통과                                                   |
| `BuildConfig` / build type           | `:app:assembleDebug`, `:app:assembleReleaseTest` 통과       |
| iOS 타겟 (commonMain)                  | `iosArm64`, `iosSimulatorArm64` 컴파일 통과                     |

- Android-KMP 라이브러리 플러그인은 build type·`BuildConfig` 미지원이라 제외했다. `:core:data`는 build type별 `buildConfigField` 5개와 `BuildConfig.DEBUG`(6개 파일)를 사용한다.
- 참고: 이 조합은 AGP 9와 호환되지 않는다. 당분간 AGP 8.13을 유지하므로 고려하지 않는다.

### DI 결정 배경

- 전체를 Koin으로 통일하는 편이 KMP 관점에서는 효율적이지만, Android 네이티브 실무에서 Hilt 비중이 높아 Android는 Hilt를 유지한다.
- shared 코드가 DI 프레임워크에 의존하지 않으므로 공유 범위(UseCase·Repository)는 줄어들지 않는다. DI는 플랫폼별 composition root에서만
  다룬다.
- 비용: `@Inject`를 제거한 클래스(64개)를 Hilt `@Provides`로 등록해야 한다.
- UseCase `@Provides` 위치는 composition root인 `:app`으로 한다 (Clean Architecture의 Main 컴포넌트, Android 모듈화 가이드의
  "app module provides actual implementation").
    - `:core:data`: `:domain`에 의존해야 하므로 core → 기능 계층 역방향 의존이 생긴다.
    - `:presentation`: 의존 방향은 맞지만 UI 계층이 객체 조립 책임까지 갖게 된다.
    - Repository `@Binds`는 구현체를 가진 `:core:data`에 둔다 (구현 소유 모듈이 바인딩).
- `javax.inject`는 JVM 전용이라 commonMain에서 사용할 수 없다. expect/actual + typealias로 `@Inject`를 유지하는 방법은
  Hilt(KSP2) 호환이 검증되지 않아 채택하지 않는다.

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
- **Kotlin 2.4 + Hilt 2.60 + AGP 9** (Xcode 26.4 공식 지원)는 `kotlin.android` 제거, Gradle 9.1+ 등 변경이 커서
  5단계 이후로 미룬다.

## 5. 미정

- Android 측 Ktor 엔진 (OkHttp 또는 CIO)
- `:core:common`, 소셜 로그인, 암호화(`crypto`) 처리
- 파일 업로드(Multipart), 토큰 인증 이관 방식 (공지 API의 인증 처리 포함)

## 6. 단계별 계획

| 단계                | 내용                                                                                                         |
|-------------------|------------------------------------------------------------------------------------------------------------|
| 0. 준비             | Kotlin/KSP/Hilt 버전업, KMP convention plugin 추가, version catalog 정리, `:core:data` KMP 전환 스파이크                |
| 1. `:core:domain` + `:domain` | 두 모듈을 함께 KMP 전환 (android + iOS 타겟, commonMain). JVM 전용 API 대체, 테스트 `androidHostTest`로 이동, `@Inject` 제거 후 Hilt `@Provides`로 등록. `:domain`(JVM)이 KMP 전환된 `:core:domain`(android 타겟)을 소비할 수 없으므로 분리하지 않는다 |
| 2. `:core:data`   | KMP 전환 (기존 코드는 전부 androidMain 유지). Android 빌드·테스트가 그대로 통과하는지 확인                                            |
| 3. 공지 API 전환 (실험) | **공지(Announcement)** API를 Retrofit → Ktor + serialization으로 전환, `:core:data` commonMain으로 이동 (인증·에러 처리 포함) |
| 4. DI + iOS 검증    | `:shared` 구성, iOS용 Koin 모듈(공지 한정), Android는 Hilt 연결, iOS에서 공지 UseCase 호출                                   |
| 5 이후              | Kotlin 2.4 + AGP 9 전환, 나머지 data 공유 여부 결정, presentation 공유(CMP) 결정, 기능 단위 모듈화                               |

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
