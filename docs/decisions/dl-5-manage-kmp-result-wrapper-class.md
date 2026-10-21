# KMP에서 Result 래퍼 클래스 관리

- 작성일: 2026-09-06

# 배경 및 목적 (Why)

- 기존 Android 네이티브 프로젝트에서 점진적으로 KMP 마이그레이션을 수행하고자 함(Android 네이티브 부분은 최대한으로 유지). 기존 모듈들을 유지한 채
  `shared` 모듈을 추가하여 iOS와 연결하고자함. 이때 `shared` 모듈은 다른 모듈을 참조할 수 없으며, 들어오는 의존 방향만 허용함.

  하지만 `shared` 에 있는 공유 UseCase 클래스가 `:core:domain` 모듈에 있는 Result 래퍼클래스를 참조할 수 없는 문제가 발생. (
  `:core:domain`은 순수 JVM 모듈이라 규칙과 별개로 iOS 타깃에서 의존하는 것 자체가 불가능함.)

  임시 조치로 `shared`, `:core:domain` 모듈 두 곳에 모두 Result 래퍼클래스를 배치함. 이로 인해 중복 코드가 발생했고 헷갈리는 상황이 발생함.

# 검토한 후보안 (Alternatives)

1. 두 모듈에 Result 중복 선언 (현재 방식)
    - 장점: 각 모듈 내부에 있는 Result만 사용하므로, 결합도가 낮은 구조를 유지할 수 있음.
    - 단점: DRY 원칙을 깨며, 만약 두 Result 클래스 중 하나가 변경되면 반드시 한 쪽도 반영해줘야함.

      또한 다른 쪽 Result를 잘못 사용하면 대부분 컴파일 에러·경고로 잡히지만, Loading처럼 object를==/when 분기로 비교하는 경우는 경고 없이 항상
      false가 되므로, else 없이 when 분기를 빠짐없이 작성해야함.

2. `shared` 모듈에서만 Result 선언
    - 장점: 한 곳에서만 선언되므로, Result 클래스를 사용할 때 헷갈릴 필요가 없음.
    - 단점: Result를 사용하려면 반드시 `shared` 모듈을 알고 있어야함. (`:core:domain` → `:shared:domain` 의존이 필요하며, 이를 위해
      `:shared:domain`에 JVM 타깃을 추가하거나 `:core:domain`을 KMP로 전환해야함.)

# 최종 결정 및 이유 (Decision)

- 1번 방식 변형을 사용하기로 했음. 각 모듈에 배치하되, 두 Result를 함께 사용하는 곳에서는 `shared` 모듈의 Result를 `SharedResult`라는
  import alias로 가져오면서 확실히 구분 해두는 방식을 채택. (클래스명은 `Result`로 유지하여, 추후 2번 방식으로 전환할 때 이름을 다시 바꾸지 않도록 함.)
- 하지만 KMP 마이그레이션이 완료되고 최종 구조에서는 `shared` 모듈 한 곳에서만 관리하는 2번 방식을 사용하기로 함. 마이그레이션 과정에서는 1번 방식을 사용하고 완료
  되면 모두 `shared` 모듈에 있는 Result를 사용하도록 리팩토링.

  (단, 각자의 네이티브 방식을 독립적으로 유지하는 방향으로 가는 경우, 1번 방식을 계속 사용할 가능성도 있음. *KMP 마이그레이션 전략*에 따라 변동될 수 있음.)

# 기타 참고사항 (Notes)

- 같은 이유로 에러 타입도 중복되어 있음 (`:shared:domain`의 `NetworkErrorType` ↔︎ `:core:domain`의 `CommonErrorType`,
  1:1 변환: `NetworkErrorTypeAsUiText.kt`). Result 통합 시 함께 정리.
- 관련 문서: `docs/spec/kmp-migration-strategy-v2.md` (4. 기술 결정(Result 부분), 6. 추후 검토(Result 통합 부분))
