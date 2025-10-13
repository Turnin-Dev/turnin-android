package com.peekr.data.profile.repository

import com.peekr.core.data.datastore.DataStoreManager
import com.peekr.core.domain.coroutine.combineWithResult
import com.peekr.core.domain.keyword.repository.KeywordRepository
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.domain.profile.model.AddKeywordResult
import com.peekr.domain.profile.model.Profile
import com.peekr.domain.profile.model.ProfilePatch
import com.peekr.domain.profile.model.toUserPatch
import com.peekr.domain.profile.repository.ProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val userKeywordRepository: UserKeywordRepository,
    private val keywordRepository: KeywordRepository,
    private val dataStoreManager: DataStoreManager,
) : ProfileRepository {
    override fun getProfile(): Flow<Result<Profile, ErrorType>> =
        combineWithResult(
            userRepository.getUserProfile(),
            userKeywordRepository.getUserKeywords(),
        ) { user, userKeywords ->
            val profile = Profile(
                displayId = user.data.user.displayId,
                name = user.data.user.name,
                friendsTotal = user.data.friendsCount,
                profileImageUrl = user.data.user.profileImageUrl,
                introduce = user.data.user.introduce,
                keywords = userKeywords.data.keywords,
            )
            Result.Success(profile)
        }

    override fun updateProfile(patch: ProfilePatch): Flow<Result<Unit, ErrorType>> =
        userRepository.updateUser(patch.toUserPatch())

    override fun addKeyword(
        keywordName: String,
        keywordDesc: String?,
    ): Flow<Result<AddKeywordResult, ErrorType>> = TODO()
//        keywordRepository.getKeywordByName(keywordName).flatMapResult { keyword ->
//            if (keyword == null) {
//                // 새롭게 키워드 등록 후 사용자 키워드 저장
//            } else {
//                // 조회된 키워드로 사용자 키워드 저장
//                val userId = dataStoreManager.
//                val createUserKeyword = CreateUserKeyword(
//
//                )
//                userKeywordRepository.createUserKeyword()
//            }
//        }
}
