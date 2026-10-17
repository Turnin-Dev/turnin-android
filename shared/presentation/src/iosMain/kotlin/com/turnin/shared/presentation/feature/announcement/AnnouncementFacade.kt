package com.turnin.shared.presentation.feature.announcement

import com.turnin.shared.domain.core.result.Result
import com.turnin.shared.domain.feature.announcement.usecase.AnnouncementUseCases
import com.turnin.shared.presentation.feature.announcement.error.AnnouncementErrorReason
import com.turnin.shared.presentation.feature.announcement.error.toReason
import com.turnin.shared.presentation.feature.announcement.model.toUiModel
import com.turnin.shared.presentation.feature.announcement.state.AnnouncementUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Swift에서 사용하는 공지 API
 *
 * 공유 UseCase를 호출하고, 결과를 Swift에서 다루기 쉬운 타입(제네릭 없는 sealed/enum, 기본 타입)으로 변환한다.
 * (SKIE가 [Flow]를 `AsyncSequence`로, sealed/enum을 Swift `enum`으로, suspend 함수를 `async`로 변환한다.)
 */
class AnnouncementFacade(
    private val useCases: AnnouncementUseCases,
) {
    /** 공지 목록 조회 (로딩 → 성공/실패) */
    fun announcements(): Flow<AnnouncementUiState> =
        useCases.getAnnouncements().map { result ->
            when (result) {
                is Result.Loading -> AnnouncementUiState.Loading
                is Result.Success -> AnnouncementUiState.Success(result.data.map { it.toUiModel() })
                is Result.Error -> AnnouncementUiState.Failure(result.error.toReason())
            }
        }

    /**
     * 공지 읽음 처리
     *
     * @return 실패 사유 (성공 시 null)
     */
    suspend fun markAsRead(announcementId: Long): AnnouncementErrorReason? =
        when (val result = useCases.markAnnouncementAsRead(announcementId)) {
            is Result.Error -> result.error.toReason()
            else -> null
        }
}
