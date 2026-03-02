package com.peekr.presentation.setting.viewmodel

import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.domain.setting.usecase.SettingUseCases
import com.peekr.presentation.setting.state.SettingContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val usecases: SettingUseCases,
) : MVIBaseViewModel<SettingContract.UiState, SettingContract.UiEvent, SettingContract.UiEffect>() {
    override fun createInitialState(): SettingContract.UiState =
        SettingContract.UiState()

    override suspend fun handleEvent(event: SettingContract.UiEvent) {
        when (event) {
            else -> {}
        }
    }
}
