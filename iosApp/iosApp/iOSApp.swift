import Shared
import SwiftUI

@main
struct iOSApp: App {
    init() {
        // 서버 URL은 Config.xcconfig(git 제외) → Info.plist(TURNIN_SERVER_URL)로 주입한다.
        let baseUrl = Bundle.main.object(forInfoDictionaryKey: "TURNIN_SERVER_URL") as? String ?? ""
        SharedEntryPoint.shared.start(baseUrl: baseUrl)
    }

    var body: some Scene {
        WindowGroup {
            AnnouncementListView()
        }
    }
}
