# 화면 분석기: 전역 NavController 방식 -> 개별 화면 방식
- 작성일: 2026-09-06

## 문제
전역에서 `NavController.currentBackStackEntry`를 관찰하며 `destination.route`를
파싱해 analytics 이름을 만들었더니 아래 문제가 발생함.
- `toRoute<Route>()`로 sealed interface 상위 타입 디코딩 시 폴리모픽 에러
  (`Polymorphic value has not been read`)
- 문자열 파싱(snake_case 변환 등)으로 우회했으나 원인 불명의 빈 문자열 등
  불안정한 케이스 존재
- `sealedSubclasses` 리플렉션 기반 자동화도 검토했으나 R8/리플렉션 동작에 대한
  확신 부족으로 보류
- 위처럼 이름을 안정적으로 뽑아내는 것 자체가 까다로우며, 파싱/리플렉션 연산이
  전역 관찰자에서 매 destination 변경마다 반복 실행되는 사소한 비용도 있었음
- `ScreenViewTracker`는 화면별로 원하는 옵션을 켜거나 끌 수 있는 파라미터를
  제공하는데, 전역 설치 방식에서는 화면마다 다른 옵션을 넘길 지점이 없어 이
  파라미터들이 사실상 무의미해짐

## 결정
- 각 Route에 `analyticsName`을 컴파일 타임 고정 문자열로 `override`
- 각 `composable<T> { }` 블록 안에서 `ScreenViewTracker(screenName = T.analyticsName)`를
  직접 호출
- 인자 있는 Route는 `toRoute<구체타입>()`으로 정확한 타입을 명시해 디코딩

## 이유
런타임 파싱/리플렉션이 전혀 없어 동작이 예측 가능함. `analyticsName`을
추상 프로퍼티로 선언해 override 누락 시 컴파일 에러로 즉시 드러남.

## 트레이드오프
Route마다 한 줄씩 보일러플레이트 필요 (감수함). 명명 규칙(소문자+언더스코어,
중복 금지)은 단위 테스트로 검증.

## 향후 재검토 가능성
Route 개수가 크게 늘어나 보일러플레이트 부담이 커지면, 리플렉션 기반 자동화나
KSP 코드 생성 방식을 재검토할 수 있음.
