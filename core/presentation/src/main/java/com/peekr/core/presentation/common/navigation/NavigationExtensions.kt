package com.peekr.core.presentation.common.navigation

import androidx.navigation.NavController

// ------------------------------ Report ------------------------------
fun NavController.navigateToReport(
    reportedId: Long,
) {
    navigate(SubGraph.Report.Root(reportedId))
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
