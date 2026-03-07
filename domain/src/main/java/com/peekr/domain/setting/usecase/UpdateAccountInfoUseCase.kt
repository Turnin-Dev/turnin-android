package com.peekr.domain.setting.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.flatMapResult
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.file.FileRepository
import com.peekr.core.domain.file.model.ImageFileDetail
import com.peekr.core.domain.file.model.Mime
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.user.model.ProfileImagePatch
import com.peekr.core.domain.user.model.UserPatch
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.domain.setting.error.SettingErrorType
import com.peekr.domain.setting.model.SettingProfileImagePatch
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 계정 정보 업데이트
 *
 * @see invoke
 */
class UpdateAccountInfoUseCase @Inject constructor(
    private val fileRepository: FileRepository,
    private val userRepository: UserRepository,
) {
    /**
     * 계정 정보를 업데이트한다.
     *
     * [profileImagePatch]로 이미지 업로드가 필요한지 판단하고 필요하다면
     * 이미지를 업로드한 후에 수정된 계정 정보를 저장한다.
     *
     * @param displayId 사용자 표시 ID
     * @param name 사용자 명
     * @param introduce 소개글
     * @param oldProfileImageUrl 기존 프로필 사진 URL
     * @param profileImagePatch 프로필 사진 패치 모델
     */
    operator fun invoke(
        displayId: String,
        name: String,
        introduce: String,
        oldProfileImageUrl: String?,
        profileImagePatch: SettingProfileImagePatch,
    ): Flow<Result<Unit, SettingErrorType>> = when (profileImagePatch) {
        SettingProfileImagePatch.Unchanged -> updateProfile(
            displayId = displayId,
            name = name,
            introduce = introduce,
            oldProfileImageUrl = oldProfileImageUrl,
            profileImagePatch = ProfileImagePatch.Unchanged,
        )

        SettingProfileImagePatch.Remove -> updateProfile(
            displayId = displayId,
            name = name,
            introduce = introduce,
            oldProfileImageUrl = oldProfileImageUrl,
            profileImagePatch = ProfileImagePatch.Remove,
        )

        is SettingProfileImagePatch.Update -> {
            val imageFileDetail = ImageFileDetail.create(
                bytes = profileImagePatch.imageBytes,
                username = name,
                mime = Mime.IMAGE_JPEG,
            )

            uploadImageFile(imageFileDetail)
                .flatMapResult { newUrl ->
                    updateProfile(
                        displayId = displayId,
                        name = name,
                        introduce = introduce,
                        oldProfileImageUrl = oldProfileImageUrl,
                        profileImagePatch = ProfileImagePatch.Update(newUrl),
                    )
                }
        }
    }

    // 프로필 저장
    private fun updateProfile(
        displayId: String,
        name: String,
        introduce: String,
        oldProfileImageUrl: String?,
        profileImagePatch: ProfileImagePatch,
    ): Flow<Result<Unit, SettingErrorType>> {
        val userPatch = UserPatch(
            displayId = DisplayId(displayId),
            name = Name(name),
            introduce = Introduce(introduce),
            oldProfileImageUrl = oldProfileImageUrl,
            profileImagePatch = profileImagePatch,
        )
        return userRepository.updateMyProfile(userPatch)
            .mapError { commonError -> SettingErrorType.CommonError(commonError) }
    }

    // 이미지 파일 업로드
    private fun uploadImageFile(
        imageFileDetail: ImageFileDetail,
    ): Flow<Result<String, SettingErrorType>> =
        fileRepository.getFileUpdatePresignedUrl(imageFileDetail.name, Mime.IMAGE_JPEG)
            .flatMapResult { presignedUrl ->
                fileRepository.uploadFile(
                    presignedUrl = presignedUrl.presignedUrl,
                    file = imageFileDetail.bytes,
                    fileName = imageFileDetail.name,
                    mime = imageFileDetail.mime,
                )
            }
            .map { result ->
                when (result) {
                    Result.Loading -> Result.Loading
                    is Result.Error -> Result.Error(SettingErrorType.CommonError(result.error))
                    is Result.Success -> {
                        if (result.data != null) {
                            Result.Success(result.data!!)
                        } else {
                            Result.Error(SettingErrorType.UploadImageFailed)
                        }
                    }
                }
            }
}
