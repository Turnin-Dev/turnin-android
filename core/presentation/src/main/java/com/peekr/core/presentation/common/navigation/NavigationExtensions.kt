package com.peekr.core.presentation.common.navigation

import androidx.navigation.NavController

// ------------------------------ Login ------------------------------
fun NavController.navigateToLogin() {
    navigate(SubGraph.Login.Root) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}

// ------------------------------ MyProfile ------------------------------
fun NavController.navigateToMyProfile() {
    navigate(Screens.MyProfile)
}

// ------------------------------ UserProfile ------------------------------
fun NavController.navigateToUserProfile(
    userId: Long,
    blockId: Long? = null,
) {
    navigate(Screens.UserProfile(userId, blockId))
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
