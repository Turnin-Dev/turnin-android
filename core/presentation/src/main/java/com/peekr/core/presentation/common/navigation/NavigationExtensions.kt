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
    userKeywordId: Long,
    userId: Long,
    keyword: String,
) {
    navigate(
        Screens.KeywordDetail(
            userKeywordId = userKeywordId,
            userId = userId,
            keyword = keyword,
        ),
    )
}
