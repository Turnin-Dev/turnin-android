import Combine
import Foundation
import Shared

/// 공지 화면 상태 (KMP 공유 UseCase를 AnnouncementFacade로, 토큰은 AuthFacade로 처리)
@MainActor
final class AnnouncementViewModel: ObservableObject {
    enum State {
        case idle
        case loading
        case loaded([AnnouncementItem])
        case failed(AnnouncementErrorReason)
    }

    @Published private(set) var state: State = .idle
    @Published private(set) var hasTokens = false

    private let announcements: AnnouncementFacade = SharedSdk.inject()
    private let auth: AuthFacade = SharedSdk.inject()

    /// 공지 목록 조회 (Kotlin Flow → SKIE AsyncSequence)
    func load() async {
        for await announcementsState in announcements.announcements() {
            switch onEnum(of: announcementsState) {
            case .loading:
                state = .loading
            case .success(let success):
                state = .loaded(success.items)
            case .failure(let failure):
                state = .failed(failure.reason)
            }
        }
    }

    /// 공지 읽음 처리 후 목록 갱신
    func markAsRead(_ item: AnnouncementItem) async {
        do {
            if let reason = try await announcements.markAsRead(announcementId: item.id) {
                state = .failed(reason)
            } else {
                await load()
            }
        } catch {
            state = .failed(.unknown)
        }
    }

    /// 토큰 갱신 실패(자동 로그아웃) 이벤트 구독 — 앱 화면이 떠 있는 동안 유지
    func observeAuthFailures() async {
        for await _ in auth.authFailures {
            hasTokens = false
        }
    }

    func refreshTokenStatus() async {
        hasTokens = (try? await auth.hasTokens())?.boolValue ?? false
    }

    /// 개발용 토큰 저장 (로그인 기능 전까지 사용)
    func saveTokens(accessToken: String, refreshToken: String) async throws {
        try await auth.saveTokens(accessToken: accessToken, refreshToken: refreshToken)
        await refreshTokenStatus()
    }

    func clearTokens() async {
        try? await auth.clearTokens()
        await refreshTokenStatus()
    }
}
