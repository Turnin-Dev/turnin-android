package com.peekr.core.domain.setting.model

/**
 * 테마 모드
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM,
    ;

    companion object {
        fun find(name: String): ThemeMode =
            entries.find { it.name == name } ?: SYSTEM
    }
}
