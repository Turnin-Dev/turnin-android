package com.peekr.domain.discover.usecase

import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.domain.discover.model.HistoryUser
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class GetMyHistoryUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): HistoryUser? {
        val coreMyProfile = userRepository.getMyProfile().first()
        if (coreMyProfile == null) return null
        return HistoryUser(
            userId = coreMyProfile.userId,
            userName = coreMyProfile.name,
            profileImageUrl = coreMyProfile.profileImageUrl,
        )
    }
}
