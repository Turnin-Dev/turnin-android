# 데이터 배치 기준: 화면 기능이 아닌 엔티티 단위로 core에 배치
- 작성일: 2026-11-06

## 문제
처음에는 클린 아키텍처 계층별 모듈(`:domain`, `:data`, `:presentation`) 안에 화면 기능별
패키지를 두고, 각 기능이 자기 Repository를 가지도록 구성함.
- 한 기능이 다른 기능의 데이터를 필요로 하면 UseCase가 다른 기능의 Repository를 참조해야
  했고, 기능 간 참조가 늘어나 복잡도가 올라갈 우려가 있었음
- 이를 피하려고 공유되는 Repository를 `:core:data`로 올리다 보니 `:data`가 비게 되었고,
  `:core:domain`에도 `friend`, `report` 등 기능처럼 보이는 패키지가 생김
- "core에는 공통 파일만 있어야 한다"는 인식과 충돌해, 피처별 Repository가 `:core:data`의
  DataSource를 직접 참조하는 방식을 대안으로 검토하게 됨

혼란의 원인은 '기능'이라는 단위가 두 가지로 쓰였기 때문임.
- 화면 기능: login, home, keywordEdit, profile 등 (`:domain`, `:presentation`의 패키지 기준)
- 데이터 개념(엔티티): friend, user, report, keyword 등 (데이터 소유권의 기준)

여러 화면이 같은 백엔드 엔티티를 나눠 쓰므로, 화면 단위로 데이터를 나누면 소유권이 어긋남.

## 결정
core의 기준은 "인프라인가"가 아니라 "여러 기능이 공유하는가"로 정함. Repository는 화면이
아니라 엔티티를 따라 배치함.

| 대상 | 위치 |
|---|---|
| 2개 이상 기능이 쓰는 엔티티의 Repository, 모델, DataSource | `:core:domain` / `:core:data` |
| 한 기능만 쓰는 API, 저장소 (예: 특정 화면 전용 임시 저장, 전용 엔드포인트) | 해당 기능의 data (`:data`의 기능 패키지, 기능별 모듈 전환 후 `:featureX:data`) |
| 여러 Repository를 조합하는 화면 로직 | 해당 기능 domain의 UseCase |
| 기능 전용이던 데이터를 다른 기능도 필요로 하게 됨 | 그때 core로 승격 |

- UseCase가 여러 core Repository를 조합하는 것은 허용함 (UseCase 본래의 역할)
- 피처 → 피처 참조는 금지하고, 피처 → core 방향 참조만 허용함
- 피처별 Repository가 core의 DataSource를 직접 참조하는 방식은 사용하지 않음

## 이유
- Repository를 엔티티당 하나로 두어야 단일 진실 공급원(SSOT)이 유지됨. 다른 기능이
  DataSource를 직접 호출하면 원래 Repository의 캐시, Room, 페이징, 무효화 로직을 거치지 않아
  정합성 버그(예: 친구를 삭제했는데 다른 화면에 남아 있음)가 생기기 쉬움
- DataSource 직접 참조 방식은 같은 데이터에 대한 매핑과 에러 변환 로직이 기능마다 중복됨
- 기능 간 복잡도는 피처 → 피처 참조에서 생기며, 피처 → core 참조는 의존 방향이 한쪽이라
  참조 수가 늘어나도 구조가 단순하게 유지됨
- 피처 모듈의 data가 비어 있고 공유 데이터가 core에 있는 구조는 Now in Android 등에서도
  쓰이는 일반적인 멀티모듈 구성임

## 트레이드오프
- `:core:domain` / `:core:data`가 엔티티 수에 비례해 커짐 (감수함)
- 기능별 data 모듈은 대부분 비어 있거나 얇게 유지됨. 잘못된 설계가 아니라 이 기준의
  자연스러운 결과로 봄
- 어떤 데이터가 "한 기능 전용"인지 판단이 필요하며, 처음에는 기능 쪽에 두었다가 공유가
  생길 때 core로 옮기는 비용이 생김

## 향후 재검토 가능성
core가 너무 커지면 기능 쪽으로 다시 내리지 않고 엔티티 단위 모듈로 분리하는 방안을 검토함
(예: `:core:data:friend`, `:core:data:user`). 기능 쪽으로 내리면 이 문서의 '문제'가 다시
발생함.
