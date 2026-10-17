package com.turnin.shared.core.auth

import com.turnin.shared.di.PlatformQualifier
import org.koin.core.module.Module
import org.koin.dsl.module

/** 인증 기능 모듈 */
internal val authModule: Module = module {
    single { AuthEvents() }
    factory {
        AuthFacade(
            tokenStorage = get(),
            authHttpClient = get(PlatformQualifier.AuthHttpClient),
            authEvents = get(),
        )
    }
}
