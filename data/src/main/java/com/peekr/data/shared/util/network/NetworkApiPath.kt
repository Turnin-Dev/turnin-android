package com.peekr.data.shared.util.network

object NetworkApiPath {
    private const val VERSION = "/v1"
    private const val BASE = "/api$VERSION"

    object Auth {
        const val ROUTE = "$BASE/auth"

        object Exists {
            const val PROVIDER = "$ROUTE/exists/provider"
            const val DISPLAY_ID = "$ROUTE/exists/displayId"
        }
    }

    object File {
        private const val ROUTE = "$BASE/file"
        const val UPLOAD = "$ROUTE/upload"
    }
}
