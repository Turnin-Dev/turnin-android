package com.turnin.shared.core.auth

import com.turnin.shared.data.core.di.SharedQualifier
import org.koin.core.module.Module
import org.koin.dsl.module

/** 인증 기능 모듈 */
internal val authModule: Module = module {
    single { AuthEvents() }
    factory {
        AuthFacade(
            tokenStorage = get(),
            authHttpClient = get(SharedQualifier.AuthHttpClient),
            authEvents = get(),
        )
    }
}
