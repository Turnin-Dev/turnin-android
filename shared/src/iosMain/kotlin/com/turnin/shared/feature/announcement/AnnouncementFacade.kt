package com.turnin.shared.feature.announcement

import com.turnin.shared.core.error.NetworkErrorType
import com.turnin.shared.core.result.Result
import com.turnin.shared.feature.announcement.error.AnnouncementErrorType
import com.turnin.shared.feature.announcement.model.Announcement
import com.turnin.shared.feature.announcement.usecase.AnnouncementUseCases
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Swift에서 사용하는 공지 API
 *
 * 공유 UseCase를 호출하고, 결과를 Swift에서 다루기 쉬운 타입(제네릭 없는 sealed/enum, 기본 타입)으로 변환한다.
 * (SKIE가 [Flow]를 `AsyncSequence`로, sealed/enum을 Swift `enum`으로, suspend 함수를 `async`로 변환한다.)
 */
class AnnouncementFacade internal constructor(
    private val useCases: AnnouncementUseCases,
) {
    /** 공지 목록 조회 (로딩 → 성공/실패) */
    fun announcements(): Flow<AnnouncementsState> =
        useCases.getAnnouncements().map { result ->
            when (result) {
                is Result.Loading -> AnnouncementsState.Loading
                is Result.Success -> AnnouncementsState.Success(result.data.map { it.toItem() })
                is Result.Error -> AnnouncementsState.Failure(result.error.toReason())
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

/** 공지 Facade 모듈 (iOS 전용) */
internal val announcementFacadeModule: Module = module {
    factory { AnnouncementFacade(useCases = get()) }
}

/** 공지 목록 상태 */
sealed interface AnnouncementsState {
    data object Loading : AnnouncementsState

    data class Success(val items: List<AnnouncementItem>) : AnnouncementsState

    data class Failure(val reason: AnnouncementErrorReason) : AnnouncementsState
}

/**
 * Swift용 공지 모델
 *
 * @property createdAtEpochMillis 생성 시각 (epoch millis)
 */
data class AnnouncementItem(
    val id: Long,
    val title: String,
    val content: String,
    val isRead: Boolean,
    val createdAtEpochMillis: Long,
)

/** 공지 요청 실패 사유 */
enum class AnnouncementErrorReason {
    /** 인증 필요 (토큰 없음·만료) */
    UNAUTHORIZED,

    /** 네트워크 연결·타임아웃 */
    NETWORK,

    /** 그 외 */
    UNKNOWN,
}

private fun Announcement.toItem() = AnnouncementItem(
    id = id.value,
    title = title,
    content = content,
    isRead = isRead,
    createdAtEpochMillis = createdAt,
)

private fun AnnouncementErrorType.toReason(): AnnouncementErrorReason = when (this) {
    is AnnouncementErrorType.Network -> when (error) {
        NetworkErrorType.Network.Unauthorized -> AnnouncementErrorReason.UNAUTHORIZED
        NetworkErrorType.Network.ConnectionFailed,
        NetworkErrorType.Exception.IO,
        NetworkErrorType.Exception.TimeOut,
        -> AnnouncementErrorReason.NETWORK

        else -> AnnouncementErrorReason.UNKNOWN
    }

    is AnnouncementErrorType.Unexpected -> AnnouncementErrorReason.UNKNOWN
}
