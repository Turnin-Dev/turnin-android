package com.turnin.shared.di

import com.turnin.shared.auth.authModule
import com.turnin.shared.notification.notificationModule
import org.koin.core.module.Module

/** iOS에 공개하는 기능 모듈 목록 (기능 추가 시 여기에 모듈을 추가한다) */
internal val featureModules: List<Module> = listOf(
    authModule,
    notificationModule,
)
