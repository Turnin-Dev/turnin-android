package com.turnin.core.designsystem.util.icon

/**
 * Turnin 아이콘 모음
 *
 * ```
 * 사용 예시
 * 1. TurninIcons.Filled.Normal.(IconName)
 * 1. TurninIcons.Filled.Bold.(IconName)
 * 2. TurninIcons.Outlined.Normal.(IconName)
 * 3. TurninIcons.Outlined.Bold.(IconName)
 * 4. TurninIcons.Default.Normal.(IconName)
 * 5. TurninIcons.Default.Bold.(IconName)
 * ```
 */
object TurninIcons {
    /** 색상이 채워진 아이콘 */
    object Filled {
        /** 색상이 채워진 아이콘 + 일반 두께 */
        object Normal

        /** 색상이 채워진 아이콘 + 두꺼운 두께 */
        object Bold
    }

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
