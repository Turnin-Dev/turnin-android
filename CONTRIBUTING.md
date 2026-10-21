# Contributing

Turnin 개발에 필요한 환경 설정, 빌드·테스트 방법, 작업 규칙을 정리한 문서입니다.

## Setup

### Requirements

- Android Studio (JDK 17 이상)
- iOS 빌드 시: macOS, Xcode 26 이상

### 설정 파일

전달받은 설정 파일을 추가합니다. 두 파일 모두 git에서 제외됩니다.

- `local.properties` (프로젝트 루트) — 없으면 Gradle 구성이 실패합니다.
- `google-services.json` — 빌드 타입별로 `app/src/{debug,release,releaseTest}/`에 둡니다.

Android Studio에서 프로젝트를 열고 `app`을 실행합니다.
iOS 앱 실행 방법은 [iosApp/README.md](iosApp/README.md)를 참고하세요.

## Build & Test

PR 전에 CI와 같은 순서로 확인합니다.

```bash
./gradlew ktlintCheck        # 코드 스타일 검사 (자동 수정: ./gradlew ktlintFormat)
./gradlew testDebugUnitTest  # 단위 테스트
./gradlew assembleDebug      # 빌드
```

빌드 타입과 범위별 테스트 실행 명령은 [README Build & Test](README.md#build--test)를 참고하세요.

## Workflow

### Branch

- `main` — 배포 브랜치. `develop` → `main` 머지 시 배포됩니다.
- `develop` — 개발 브랜치. 기능 브랜치는 여기서 만들고 여기로 PR합니다.
- 기능 브랜치 — `<type>/PK-<번호>-<설명>` (예: `feat/PK-153-add-event`)

### Commit

```
[PK-<번호>] <type>: <한국어 설명>
```

예: `[PK-143] feat: 공지 유스케이스 추가`, `[PK-143] refactor(사용자 프로필): ...`

### Pull Request

- PR 제목에도 JIRA 키를 포함합니다. 예: `[PK-171] feat: ...`
- [PR 템플릿](.github/pull_request_template.md)에 맞춰 작성합니다.

### CI / CD

| 트리거          | 동작                                                         |
|--------------|------------------------------------------------------------|
| `main` 대상 PR | ktlint → 단위 테스트 → 디버그 빌드                                   |
| `main` 푸시    | 릴리즈 빌드 → Firebase App Distribution, Google Play 비공개 테스트 배포 |

## Conventions

- 아키텍처와 기능 패키지 구성: [README Architecture](README.md#architecture)
- 에러 처리: [ERROR_STRATEGY.md](ERROR_STRATEGY.md)
- 공유 모듈(KMP)로 기능 옮기기: [KMP 마이그레이션 전략](docs/spec/kmp-migration-strategy-v2.md)의 기능 이동 체크리스트
