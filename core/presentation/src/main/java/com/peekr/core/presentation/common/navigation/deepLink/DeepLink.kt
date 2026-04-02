package com.peekr.core.presentation.common.navigation.deepLink

/** 딥링크 */
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

    // TODO: 만약 사용자 프로필에 사진 URL을 넘겨야 하는 상황이면,
    //  인코딩을 해줘야 하지만 URI 방식이 아닌 Type-Safe 방식으로 변경한다면 불필요하다.
    object Pattern {
        const val PROFILE = "${Uri.PROFILE}/{userId}?blockId={blockId}"
        const val KEYWORD_DETAIL = "${Uri.KEYWORD_DETAIL}/{userKeywordId}/{userId}"
        const val NOTIFICATIONS = Uri.NOTIFICATIONS
    }

    /**
     * URI 생성을 담당하는 빌더
     *
     * 앱 내부(NavController.navigate)와 외부(Notification Intent)에서 공통으로 사용
     */
    object Builder {
        /**
         * 프로필 화면 URI 생성
         *
         * @param userId 필수 (null이거나 0 이하면 null 반환)
         */
        fun profile(userId: Long?): String? {
            if (userId == null || userId <= 0) return null

            return "${Uri.PROFILE}/$userId"
        }

        /**
         * 키워드 상세 화면 URI 생성
         *
         * @param userKeywordId 필수
         * @param userId 필수
         */
        fun keywordDetail(userKeywordId: Long?, userId: Long?): String? {
            if (userKeywordId == null || userId == null) return null
            if (userKeywordId <= 0 || userId <= 0) return null

            return "${Uri.KEYWORD_DETAIL}/$userKeywordId/$userId"
        }

        /**
         * 알림 목록 화면 URI 생성
         * 데이터가 잘못되었을 때의 Fallback용으로도 사용 가능
         */
        fun notifications(): String = Uri.NOTIFICATIONS
    }
}
