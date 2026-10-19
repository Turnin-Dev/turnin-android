import SwiftUI

/// 개발용 토큰 입력 화면 (로그인 기능 전까지 사용, DEBUG 빌드에서만 노출)
struct DevTokenView: View {
    @ObservedObject var viewModel: AnnouncementViewModel
    @Environment(\.dismiss) private var dismiss
    @State private var accessToken = ""
    @State private var refreshToken = ""
    @State private var errorMessage: String?

    var body: some View {
        NavigationStack {
            Form {
                Section("액세스 토큰") {
                    SecureField("accessToken", text: $accessToken)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled()
                }
                Section("리프레시 토큰") {
                    SecureField("refreshToken", text: $refreshToken)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled()
                }
                if let errorMessage {
                    Text(errorMessage).foregroundStyle(.red)
                }
                Section {
                    Button("토큰 삭제", role: .destructive) {
                        Task {
                            await viewModel.clearTokens()
                            dismiss()
                        }
                    }
                }
            }
            .navigationTitle("개발용 토큰")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("닫기") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("저장") {
                        Task {
                            do {
                                try await viewModel.saveTokens(accessToken: accessToken, refreshToken: refreshToken)
                                dismiss()
                                await viewModel.load()
                            } catch {
                                errorMessage = "Keychain 저장에 실패했습니다."
                            }
                        }
                    }
                    .disabled(accessToken.isEmpty || refreshToken.isEmpty)
                }
            }
        }
    }
}
