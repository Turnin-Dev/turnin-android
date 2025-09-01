package com.peekr.presentation.common.util

import androidx.compose.ui.unit.dp

/** 모든 화면에서 공통적으로 사용하는 화면 수치 값들 */
object ScreenTokens {
    /** 공통 수평 패딩 */
    val HorizontalPadding = 20.dp

    /**
     * 공통 수평 패딩 (터치 타겟과 함께 사용할 때의 내부 콘텐츠 좌우 여백)
     * - 전제: 실제 클릭/터치 타겟의 최소 크기 48.dp는 별도 컴포넌트/컨테이너에서 보장
     */
    val HorizontalPaddingWithTouchTarget = 10.dp

    /** 하단에 버튼 배치시 하단 패딩 */
    val BottomButtonPadding = 20.dp
}
