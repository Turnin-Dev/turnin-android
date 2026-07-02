package com.turnin.presentation.setting.viewmodel

import androidx.lifecycle.ViewModel
import com.turnin.core.common.util.AppVersionProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VersionInfoViewModel @Inject constructor(
    private val appVersionProvider: AppVersionProvider,
) : ViewModel() {
    val appVersion = "v${appVersionProvider.versionName}"
}
