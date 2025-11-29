package com.peekr.core.designsystem.util.token

import androidx.compose.ui.unit.dp

/** 모든 화면에서 공통적으로 사용하는 화면 수치 값들 */
object ScreenTokens {
    /** 공통 수평 패딩 */
    val HorizontalPadding = 20.dp

    /**
     * 공통 수평 패딩 (터치 타겟과 함께 사용할 때의 내부 콘텐츠 좌우 여백)
     *
     * 보통 아이콘 버튼이 포함된 레이아웃에서 사용되며, 터치타겟 사이즈에서 아이콘 사이즈를 제외한 값이다.
     */
    val HorizontalPaddingWithTouchTarget = HorizontalPadding - 10.dp

    /** 하단에 버튼 배치시 하단 패딩 */
    val BottomButtonPadding = 20.dp
}
