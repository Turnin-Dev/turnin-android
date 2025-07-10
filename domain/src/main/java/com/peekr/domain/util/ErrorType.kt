package com.peekr.domain.util

sealed interface ErrorType : Error {
    enum class Test : ErrorType {
        Unexpected,
    }
}
