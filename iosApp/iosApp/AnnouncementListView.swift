import Shared
import SwiftUI

struct AnnouncementListView: View {
    @StateObject private var viewModel = AnnouncementViewModel()
    @State private var isTokenSheetPresented = false

    var body: some View {
        NavigationStack {
            content
                .navigationTitle("공지")
                .toolbar {
                    ToolbarItem(placement: .topBarTrailing) {
                        Button {
                            Task { await viewModel.load() }
                        } label: {
                            Image(systemName: "arrow.clockwise")
                        }
                    }
                    #if DEBUG
                    ToolbarItem(placement: .topBarLeading) {
                        Button(viewModel.hasTokens ? "토큰 ✓" : "토큰") {
                            isTokenSheetPresented = true
                        }
                    }
                    #endif
                }
                .sheet(isPresented: $isTokenSheetPresented) {
                    DevTokenView(viewModel: viewModel)
                }
        }
        .task { await viewModel.refreshTokenStatus() }
        .task { await viewModel.observeAuthFailures() }
        .task { await viewModel.load() }
    }

    @ViewBuilder
    private var content: some View {
        switch viewModel.state {
        case .idle, .loading:
            ProgressView()
        case .loaded(let items) where items.isEmpty:
            Text("공지가 없습니다.")
                .foregroundStyle(.secondary)
        case .loaded(let items):
            List(items, id: \.id) { item in
                AnnouncementRow(item: item)
                    .contentShape(Rectangle())
                    .onTapGesture {
                        guard !item.isRead else { return }
                        Task { await viewModel.markAsRead(item) }
                    }
            }
        case .failed(let reason):
            VStack(spacing: 12) {
                Text(reason.message)
                Button("다시 시도") {
                    Task { await viewModel.load() }
                }
            }
        }
    }
}

private struct AnnouncementRow: View {
    let item: UiAnnouncement

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack {
                if !item.isRead {
                    Circle().fill(.blue).frame(width: 8, height: 8)
                }
                Text(item.title).font(.headline)
            }
            Text(item.content)
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .lineLimit(2)
            Text(Self.dateFormatter.string(from: Date(timeIntervalSince1970: TimeInterval(item.createdAtEpochSeconds))))
                .font(.caption)
                .foregroundStyle(.tertiary)
        }
        .padding(.vertical, 4)
    }

    // Android와 같은 yyyy.MM.dd 형식 (기기 시간대 기준). 행마다 만들지 않도록 공유한다.
    // TODO: Android처럼 7일 이내는 상대 시간("n분 전")으로 표시 — UI 공유(CMP) 시 Android 로직과 함께 적용 예정
    private static let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = "yyyy.MM.dd"
        return formatter
    }()
}

private extension AnnouncementErrorReason {
    var message: String {
        switch self {
        case .unauthorized: "로그인이 필요합니다. (개발용: 상단의 토큰 버튼으로 토큰을 입력하세요)"
        case .network: "네트워크 연결을 확인해주세요."
        case .unknown: "알 수 없는 오류가 발생했습니다."
        }
    }
}
