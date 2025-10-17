package com.peekr.domain.profile.error

import com.peekr.core.domain.util.DomainError

sealed interface ProfileErrorType : DomainError {
    data object UserIdNotFound : ProfileErrorType
}
