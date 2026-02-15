package com.peekr.core.presentation.common.navigation

import androidx.navigation.NavController

// ------------------------------ MyProfile ------------------------------
fun NavController.navigateToMyProfile() {
    navigate(Screens.MyProfile)
}

// ------------------------------ UserProfile ------------------------------
fun NavController.navigateToUserProfile(userId: Long) {
    navigate(Screens.UserProfile(userId))
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
