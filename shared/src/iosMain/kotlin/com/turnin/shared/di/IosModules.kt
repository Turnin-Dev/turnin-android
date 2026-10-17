package com.turnin.shared.di

import com.turnin.shared.core.auth.authModule
import com.turnin.shared.feature.announcement.announcementFacadeModule
import org.koin.core.module.Module

/** iOS 전용 모듈 목록: Swift에 공개하는 Facade (기능 추가 시 여기에 Facade 모듈을 추가한다) */
internal val iosModules: List<Module> = listOf(
    authModule,
    announcementFacadeModule,
)
