package com.peekr.presentation.keywordDetail.state

import com.peekr.core.presentation.util.UiText

data class MyKeywordDetailState(
    val keyword: String = "",
    val description: String = "",
    val loading: Boolean = false,
    val error: UiText? = null,
)
