package com.turnin.core.presentation.common.snackbar

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SnackbarModule {
    @Binds
    @Singleton
    abstract fun bindSnackbarController(
        impl: SnackbarControllerImpl,
    ): SnackbarController
}
