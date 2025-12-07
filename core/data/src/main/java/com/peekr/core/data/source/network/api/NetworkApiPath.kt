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
        const val PATCH_OFFSET = "$ROUTE/offset"
        const val DESCRIPTION = "$ROUTE/description"
    }

    object File {
        private const val ROUTE = "$BASE/file"
        const val UPLOAD = "$ROUTE/upload"
    }

    object User {
        const val ROUTE = "$BASE/user"
        const val INTRODUCE = "$ROUTE/introduce"

        object Profile {
            const val ROUTE = "${User.ROUTE}/profile"
            const val ME = "$ROUTE/me"
        }
    }

    object Report {
        const val ROUTE = "$BASE/report"
        const val REASON = "$ROUTE/reason"
    }

    object Friend {
        const val ROUTE = "$BASE/friend"
    }
}
