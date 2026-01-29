package com.peekr.core.data.source.network.api

object NetworkApiPath {
    private const val VERSION = "/v1"
    private const val BASE = "/api$VERSION"

    object Auth {
        const val ROUTE = "$BASE/auth"

        const val REFRESH = "$ROUTE/refresh"
        const val LOGIN = "$ROUTE/login"
        const val REGISTER = "$ROUTE/register"

        object Exists {
            const val PROVIDER = "$ROUTE/exists/provider"
            const val DISPLAY_ID = "$ROUTE/exists/displayId"
        }
    }

    object Keyword {
        const val ROUTE = "$BASE/keyword"
        const val ID = "$ROUTE/id"
        const val NAME = "$ROUTE/name"
    }

    object UserKeyword {
        const val ROUTE = "$BASE/user-keyword"
        const val DETAIL = "$ROUTE/{userKeywordId}/detail"
    }

    object File {
        private const val ROUTE = "$BASE/file"
        const val UPLOAD = "$ROUTE/upload"
    }

    object User {
        const val ROUTE = "$BASE/user"
        const val INTRODUCE = "$ROUTE/introduce"
        const val MY_PROFILE = "$ROUTE/me/profile"
        const val USER_PROFILE = "$ROUTE/{userId}/profile"
        const val MY_KEYWORDS = "$ROUTE/me/keywords"
        const val USER_KEYWORDS = "$ROUTE/{userId}/keywords"
    }

    object Report {
        const val ROUTE = "$BASE/report"
        const val REASON = "$ROUTE/reason"
    }

    object Friend {
        const val ROUTE = "$BASE/friend"
        const val STATUS = "$ROUTE/status"
        const val LIST = "$ROUTE/list"
    }

    object Discover {
        const val ROUTE = "$BASE/discover"
    }

    object Feed {
        const val ROUTE = "$BASE/feed"
    }
}
