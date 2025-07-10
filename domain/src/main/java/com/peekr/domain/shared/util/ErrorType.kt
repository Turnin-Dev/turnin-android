package com.peekr.domain.shared.util

sealed interface ErrorType : Error {
    enum class Test : ErrorType {
        Unexpected,
    }
}
