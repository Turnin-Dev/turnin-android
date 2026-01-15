package com.peekr.presentation.keywordDetail.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.keywordDetail.usecase.GetDescriptionUseCase
import com.peekr.presentation.R
import com.peekr.presentation.keywordDetail.state.KeywordDetailContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class KeywordDetailViewModel @Inject constructor(
    private val getDescriptionUseCase: GetDescriptionUseCase,
    savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<KeywordDetailContract.UiState, KeywordDetailContract.UiEvent, KeywordDetailContract.UiEffect>() {
    private val currentUserKeywordId: Long by lazy {
        requireNotNull(savedStateHandle.get<Long>("userKeywordId"))
    }
    private val currentUserId: Long by lazy {
        requireNotNull(savedStateHandle.get<Long>("userId"))
    }

    override fun createInitialState(): KeywordDetailContract.UiState =
        KeywordDetailContract.UiState()

    override suspend fun loadInitialData() {
        val initResult = initNavArgumentData()
        // initNavArgumentData 가 실패할 경우(false를 반환할 경우)
        // 에러 처리를 하고 프로필 로드 기능을 중단한다(다른 기능이 실행될 수 없다).
        if (!initResult) return
    }

    private fun initNavArgumentData(): Boolean = runCatching {
        currentUserKeywordId
        currentUserId
    }
        .onFailure {
            updateState {
                this.copy(
                    error = UiText.StringResource(R.string.keyword_detail_screen_error_load_failed),
                )
            }
        }
        .isSuccess

    override suspend fun handleEvent(event: KeywordDetailContract.UiEvent) {
        when (event) {
            else -> {}
        }
    }
}
