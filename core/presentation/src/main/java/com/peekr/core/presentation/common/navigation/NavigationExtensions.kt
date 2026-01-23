package com.peekr.core.presentation.common.navigation

import androidx.navigation.NavController

// ------------------------------ Report ------------------------------
fun NavController.navigateToReport(
    reportedId: Long?,
    reportedUserKeywordId: Long?,
) {
    navigate(SubGraph.Report.Root(reportedId, reportedUserKeywordId)) {
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
