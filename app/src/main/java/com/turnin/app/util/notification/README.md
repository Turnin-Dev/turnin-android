# NotificationSyncManager

FCM 토큰 등록/해제를 **단일 진입점**으로 관리하는 알림 동기화 매니저.

---

## 설명

알림 상태는 여러 조건에 따라 변해야 한다.

- 알림 **권한** (OS 수준)
- 앱 내 **푸시 알림 토글** 설정
- **로그인 / 로그아웃** 여부
- FCM **토큰 갱신**

이 조건들이 각자 다른 곳에서 트리거되기 때문에, `NotificationSyncManager.sync()` 하나로 모아서 처리한다.

---

## 호출 시점

| 시점 | 호출 위치 |
|---|---|
| 앱 시작 (로그인 상태) | `MainViewModel.init` |
| 로그인 완료 | `MainViewModel.onLogin()` |
| 앱 복귀 (설정 화면에서 돌아올 때) | `MainActivity.onResume` → `MainViewModel.syncNotificationState()` |
| 푸시 알림 토글 변경 | `NotificationSettingViewModel.togglePushNotificationAndSync()` |
| FCM 토큰 갱신 | `TurninFirebaseMessagingService.onNewToken()` |

---

## sync() 흐름

```
sync() 호출
    │
    ├─ Mutex로 중복 실행 방지
    │
    ├─ 조건 판단
    │   ├─ hasPermission  (OS 알림 권한)
    │   └─ isEnabled      (앱 내 푸시 토글)
    │       └─ shouldRegister = hasPermission && isEnabled
    │
    └─ 상태 분기
        ├─ shouldRegister == true  &&  lastState == REGISTERED   → 변경 없음 (skip)
        ├─ shouldRegister == false &&  lastState == DEACTIVATED  → 변경 없음 (skip)
        ├─ shouldRegister == true  → registerTokenAndSubscribe()
        └─ shouldRegister == false → unsubscribe()
```

### registerTokenAndSubscribe()
1. FCM 토큰 조회
2. 서버에 토큰 등록
3. 로컬 상태 → `REGISTERED`
4. FCM 토픽 구독

### unsubscribe()
1. 이미 `DEACTIVATED`면 skip
2. FCM 토큰 조회
3. 서버에 토큰 비활성화 요청
4. 로컬 상태 → `DEACTIVATED`
5. FCM 토픽 구독 해제

---

## 핵심 설계 포인트

- **Mutex** — 이미 실행 중이면 대기하지 않고 즉시 skip (tryLock으로 중복 실행 방지)
- **lastState 캐시** — 불필요한 서버 요청 방지 (이미 동일 상태면 skip)
- **단일 책임** — 권한/토글/토큰/로그인 상태를 모두 여기서 판단, 호출자는 그냥 `sync()`만 부르면 됨
