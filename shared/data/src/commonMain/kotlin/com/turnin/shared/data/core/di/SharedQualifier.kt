package com.turnin.shared.data.core.di

import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named

/**
 * 공유 Koin 모듈의 한정자: 플랫폼(Android·iOS)이 공유 모듈에 제공해야 하는 객체를 구분한다.
 *
 * Android(`:app`의 Koin 브릿지)와 iOS(`SharedEntryPoint`)가 같은 한정자로 등록해야 공유 모듈(`sharedModules`)이 조회할 수 있다.
 * Koin은 정의의 반환 타입으로 조회하므로 등록 시 타입을 명시한다. (예: `single<CoroutineDispatcher>(IoDispatcher) { ... }`)
 */
object SharedQualifier {
    /** 인증(토큰 첨부·갱신)이 적용된 HttpClient */
    val AuthHttpClient: Qualifier = named("AuthHttpClient")

    /** IO 디스패처 (Android: Hilt `@IO`, iOS: `Dispatchers.IO`) */
    val IoDispatcher: Qualifier = named("IoDispatcher")
}
