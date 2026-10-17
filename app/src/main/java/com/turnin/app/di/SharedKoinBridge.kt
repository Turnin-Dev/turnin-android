package com.turnin.app.di

import com.turnin.core.common.coroutine.IO
import com.turnin.core.data.BuildConfig
import com.turnin.core.data.di.TokenOkHttpClient
import com.turnin.shared.data.core.di.SharedQualifier
import com.turnin.shared.data.core.network.createHttpClient
import com.turnin.shared.di.sharedModules
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import org.koin.core.Koin
import org.koin.dsl.koinApplication
import org.koin.dsl.module

/**
 * 공유 모듈(`:shared`) Koin → Hilt 브릿지
 *
 * - Hilt가 가진 플랫폼 재료([SharedQualifier])를 넘겨 공유 Koin 모듈([sharedModules])을 조립한다.
 * - 전역 `startKoin` 대신 별도 Koin 컨테이너를 Hilt `@Singleton`으로 하나만 만든다.
 *   (`single` 인스턴스가 Koin `Module` 객체에 보관되므로 컨테이너가 둘 이상 생기면 안 된다.)
 * - 기능별 객체는 `Shared<기능>Module`에서 이 컨테이너로부터 꺼내 Hilt에 제공한다.
 */
@Module
@InstallIn(SingletonComponent::class)
object SharedKoinBridge {
    @Provides
    @Singleton
    fun provideSharedKoin(
        @TokenOkHttpClient okHttpClient: OkHttpClient,
        @IO ioDispatcher: CoroutineDispatcher,
    ): Koin = koinApplication {
        modules(
            listOf(
                module {
                    single<HttpClient>(SharedQualifier.AuthHttpClient) {
                        createHttpClient(okHttpClient = okHttpClient, baseUrl = BuildConfig.TURNIN_SERVER_URL)
                    }
                    single<CoroutineDispatcher>(SharedQualifier.IoDispatcher) { ioDispatcher }
                },
            ) + sharedModules,
        )
    }.koin
}
