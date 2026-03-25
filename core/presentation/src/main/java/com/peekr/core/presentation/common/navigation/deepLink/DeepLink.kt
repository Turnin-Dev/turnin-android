package com.peekr.core.presentation.common.navigation.deepLink

object DeepLink {
    const val SCHEME = "peekr"

    object Path {
        const val PROFILE = "profile"
        const val KEYWORD_DETAIL = "keyword_detail"
        const val NOTIFICATIONS = "notifications"
    }

    object Uri {
        const val PROFILE = "$SCHEME://${Path.PROFILE}"
        const val KEYWORD_DETAIL = "$SCHEME://${Path.KEYWORD_DETAIL}"
        const val NOTIFICATIONS = "$SCHEME://${Path.NOTIFICATIONS}"
    }

    object Pattern {
        const val PROFILE = "${Uri.PROFILE}/{userId}?blockId={blockId}"
        const val KEYWORD_DETAIL = "${Uri.KEYWORD_DETAIL}/{userKeywordId}/{userId}"
        const val NOTIFICATIONS = "${Uri.NOTIFICATIONS}"
    }
}
