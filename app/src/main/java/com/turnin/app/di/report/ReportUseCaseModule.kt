package com.turnin.app.di.report

import com.turnin.core.domain.report.repository.ReportRepository
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.domain.report.usecase.GetReportReasonsUseCase
import com.turnin.domain.report.usecase.ReportUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 신고 UseCase 의존성 제공
 */
@Module
@InstallIn(SingletonComponent::class)
object ReportUseCaseModule {
    @Provides
    fun provideGetReportReasonsUseCase(
        reportRepository: ReportRepository,
    ): GetReportReasonsUseCase =
        GetReportReasonsUseCase(
            reportRepository = reportRepository,
        )

    @Provides
    fun provideReportUseCase(
        userRepository: UserRepository,
        reportRepository: ReportRepository,
    ): ReportUseCase =
        ReportUseCase(
            userRepository = userRepository,
            reportRepository = reportRepository,
        )
}
