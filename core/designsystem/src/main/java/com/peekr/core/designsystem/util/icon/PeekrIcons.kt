package com.peekr.core.designsystem.util.icon

/**
 * Peekr 아이콘 모음
 *
 * ```
 * 사용 예시
 * 1. PeekrIcons.Filled.(IconName)
 * 2. PeekrIcons.Outlined.Normal.(IconName)
 * 3. PeekrIcons.Outlined.Bold.(IconName)
 * 4. PeekrIcons.Default.Normal.(IconName)
 * 5. PeekrIcons.Default.Bold.(IconName)
 * ```
 */
object PeekrIcons {
    /** 색상이 채워진 아이콘 */
    object Filled

    /** 테두리만 있는 아이콘 */
    object Outlined {
        /** 테두리만 있는 아이콘 + 일반 두께 */
        object Normal

        /** 테두리만 있는 아이콘 + 두꺼운 두께 */
        object Bold
    }

    /** 기본 형태의 아이콘 */
    object Default {
        /** 기본 형태의 아이콘 + 일반 두께 */
        object Normal

        /** 기본 형태의 아이콘 + 일반 두께 */
        object Bold
    }
}
