package com.peekr.core.data.network

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
    }

    object UserKeyword {
        const val ROUTE = "$BASE/user-keyword"
    }

    object File {
        private const val ROUTE = "$BASE/file"
        const val UPLOAD = "$ROUTE/upload"
    }

    object User {
        const val ROUTE = "$BASE/user"
    }
}
