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
    let item: AnnouncementItem

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
            Text(Date(timeIntervalSince1970: TimeInterval(item.createdAtEpochMillis) / 1000), style: .date)
                .font(.caption)
                .foregroundStyle(.tertiary)
        }
        .padding(.vertical, 4)
    }
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
