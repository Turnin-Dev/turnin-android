package com.turnin.core.data.di

import com.turnin.core.common.coroutine.IO
import com.turnin.core.data.BuildConfig
import com.turnin.core.data.source.network.ktor.createHttpClient
import com.turnin.core.domain.announcement.repository.AnnouncementRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import org.koin.core.Koin
import org.koin.core.module.Module as KoinModule
import org.koin.dsl.koinApplication
import org.koin.dsl.module

/**
 * KMP 공유 data 객체(Koin)를 Hilt 그래프로 넘기는 브릿지
 *
 * - 전역 `startKoin` 대신 별도 Koin 컨텍스트를 사용해 시작 순서·다른 Koin 사용처와의 충돌에 영향을 받지 않는다.
 * - **앱에 하나만 존재해야 한다.** 인스턴스마다 Koin 컨테이너와 공유 객체가 따로 생성되므로,
 *   플랫폼 재료가 더 필요하면 [SharedDataBridgeModule.provideSharedDataKoin]의 인자와 [SharedQualifier]를 추가한다.
 *
 * @param platformModule 플랫폼이 제공하는 객체([SharedQualifier])를 담은 Koin 모듈
 */
class SharedDataKoin(platformModule: KoinModule) {
    private val koin: Koin = koinApplication { modules(platformModule, sharedDataModule) }.koin

    /** 공지 리포지토리 */
    fun announcementRepository(): AnnouncementRepository = koin.get()
}

@Module
@InstallIn(SingletonComponent::class)
object SharedDataBridgeModule {
    /**
     * 플랫폼 재료는 Hilt → Koin, 공유 객체는 Koin → Hilt 방향으로만 흐른다.
     *
     * 인증 HttpClient는 기존 토큰 OkHttpClient(토큰 첨부·갱신·로그아웃, 캐시, 로깅)를 Ktor OkHttp 엔진에 그대로 사용한다.
     * Retrofit과 같은 TokenAuthenticator를 공유하므로 토큰 갱신이 중복 실행되지 않는다.
     */
    @Provides
    @Singleton
    fun provideSharedDataKoin(
        @TokenOkHttpClient tokenOkHttpClient: OkHttpClient,
        @IO ioDispatcher: CoroutineDispatcher,
    ): SharedDataKoin = SharedDataKoin(
        platformModule = module {
            single<HttpClient>(SharedQualifier.AuthHttpClient) {
                createHttpClient(
                    engine = OkHttp.create { preconfigured = tokenOkHttpClient },
                    baseUrl = BuildConfig.TURNIN_SERVER_URL,
                )
            }
            single<CoroutineDispatcher>(SharedQualifier.IoDispatcher) { ioDispatcher }
        },
    )

    @Provides
    @Singleton
    fun provideAnnouncementRepository(sharedDataKoin: SharedDataKoin): AnnouncementRepository =
        sharedDataKoin.announcementRepository()
}
