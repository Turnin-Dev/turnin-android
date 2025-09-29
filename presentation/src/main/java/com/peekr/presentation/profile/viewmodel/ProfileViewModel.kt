package com.peekr.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import com.peekr.presentation.profile.state.ProfileSideEffect
import com.peekr.presentation.profile.state.ProfileState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class ProfileViewModel : ViewModel(), ContainerHost<ProfileState, ProfileSideEffect> {
    override val container: Container<ProfileState, ProfileSideEffect> =
        container<ProfileState, ProfileSideEffect>(initialState = ProfileState())
}
