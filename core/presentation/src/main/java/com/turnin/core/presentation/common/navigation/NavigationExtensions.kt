package com.turnin.core.presentation.common.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.turnin.core.presentation.common.navigation.args.UserProfileArgs
import com.turnin.core.presentation.ui.model.UiSocialLoginProvider

// ------------------------------ Login ------------------------------
fun NavController.navigateToLogin() {
    navigate(SubGraph.Login.Root) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}

// ------------------------------ Register ------------------------------
fun NavController.navigateToRegister(
    provider: UiSocialLoginProvider,
    providerId: String,
) {
    navigate(
        SubGraph.Register.Root(
            provider = provider,
            providerId = providerId,
        ),
    )
}

// ------------------------------ MyProfile ------------------------------
fun NavController.navigateToMyProfile() {
    navigate(Screens.MyProfile)
}

// ------------------------------ UserProfile ------------------------------
fun NavController.navigateToUserProfile(args: UserProfileArgs) {
    navigate(
        Screens.UserProfile(
            userId = args.userId,
            userName = args.userName,
            displayId = args.displayId,
            profileImageUrl = args.profileImageUrl,
            blockId = args.blockId,
            forceRefresh = args.forceRefresh,
        ),
    )
}

// ------------------------------ Report ------------------------------
fun NavController.navigateToReport(
    reportedId: Long?,
    reportedUserKeywordId: Long?,
    onlyReport: Boolean,
) {
    navigate(SubGraph.Report.Root(reportedId, reportedUserKeywordId, onlyReport)) {
        launchSingleTop = true
    }
}

// ------------------------------ BlockModal ------------------------------
fun NavController.navigateToBlockModal(userId: Long?) {
    navigate(SubGraph.BlockModal.Root(userId)) {
        popUpTo(SubGraph.Report.SelectReportBlock) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

// ------------------------------ BlockList ------------------------------
fun NavController.navigateToBlockList() {
    navigate(Screens.BlockList)
}

// ------------------------------ KeywordEdit ------------------------------
fun NavController.navigateToKeywordEdit(userKeywordId: Long?) {
    navigate(Screens.KeywordEdit(userKeywordId))
}

// ------------------------------ KeywordDetail ------------------------------
fun NavController.navigateToKeywordDetail(
    userId: Long,
    userKeywordId: Long,
) {
    navigate(
        Screens.KeywordDetail(
            userKeywordId = userKeywordId,
            userId = userId,
        ),
    )
}

// ------------------------------ FriendsList ------------------------------
fun NavController.navigateToFriendsList(userId: Long) {
    navigate(Screens.FriendList(userId))
}

// ------------------------------ Notification ------------------------------
fun NavController.navigateToNotification() {
    navigate(Screens.Notifications)
}

// ------------------------------ Setting ------------------------------
fun NavController.navigateToSetting() {
    navigate(SubGraph.Setting.Root)
}

fun NavController.navigateToCropProfileImage(uri: String) {
    navigate(SubGraph.Setting.CropProfileImage(uri))
}

fun NavController.navigateToVersionInfo() {
    navigate(SubGraph.Setting.VersionInfo)
}

fun NavController.navigateToQna(qnaUrl: String) {
    navigate(SubGraph.Setting.Qna(qnaUrl))
}

fun NavController.navigateToNotificationSetting() {
    navigate(SubGraph.Setting.NotificationSetting)
}

// ------------------------------ TermsAgreement ------------------------------
fun NavController.navigateToTermsOfService() {
    navigate(Screens.TermsOfService)
}

fun NavController.navigateToPrivacyPolicy() {
    navigate(Screens.PrivacyPolicy)
}

// ------------------------------ BottomBar Item ------------------------------
fun NavController.navigateToBottomBarItem(route: SubGraph) {
    // 현재 선택된 탭과 다르다면 그냥 navigate 수행
    // 첫 번째 화면만 스택에 쌓이므로 뒤로가기 시 첫 번째 화면으로 이동한다.
    navigate(route) {
        graph.findStartDestination().route?.let {
            popUpTo(it) {
                saveState = true
            }
        }

        launchSingleTop = true
        restoreState = true
    }
}
