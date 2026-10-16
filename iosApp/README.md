# iosApp

KMP 공유 모듈(`:shared`)의 `Shared` 프레임워크를 사용하는 SwiftUI 앱이다.
구조와 규칙은 루트 `CLAUDE.md`의 iOS 항목과 `docs/spec/kmp-migration-strategy.md`를 참고한다.

## 요구 사항

- macOS + Xcode 26 이상 (배포 대상 iOS 26.0)
- JDK 17 이상 (`./gradlew` 실행용)
- 루트 `local.properties` (Android와 동일, Gradle 구성에 필요)

## 처음 실행할 때

1. 서버 URL 설정 파일을 만든다.

   ```bash
   cp iosApp/Config.xcconfig.example iosApp/Config.xcconfig
   ```

   `Config.xcconfig`에 Debug/Release 서버 URL을 입력한다. 이 파일은 git에서 제외된다.
   - xcconfig에서 `//`는 주석이므로 `https:/$()/host` 형태로 작성한다.
   - Debug/Release 값은 `TURNIN_SERVER_URL[config=Debug]`, `TURNIN_SERVER_URL[config=Release]`로 나눈다.

2. `iosApp/iosApp.xcodeproj`를 Xcode로 열고 iPhone 시뮬레이터를 선택해 실행한다.
   - 빌드 시 Run Script가 `./gradlew :shared:embedAndSignAppleFrameworkForXcode`로 Kotlin 프레임워크를 빌드한다. 첫 빌드는 몇 분 걸린다.
   - Run Script, User Script Sandboxing(No), `Config.xcconfig` 연결, `Info.plist`의 `TURNIN_SERVER_URL`은 프로젝트에 이미 설정되어 있다.

3. 로그인 화면이 아직 없으므로 좌측 상단 **토큰** 버튼에서 access/refresh 토큰을 입력한다. 토큰은 Keychain에 저장된다.

## 문제 해결

- **`No such module 'Shared'`**: Build Settings → Framework Search Paths에 `$(SRCROOT)/../shared/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)`를 추가한다.
- **Run Script에서 Java를 찾지 못함**: 스크립트의 `cd` 위에 `export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"`을 추가한다.
- **서버 URL이 비어 있음**: `Config.xcconfig`가 있는지, `https:/$()/` 형식인지 확인한다.
