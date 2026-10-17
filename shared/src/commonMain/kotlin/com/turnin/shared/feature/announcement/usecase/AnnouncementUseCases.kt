package com.turnin.shared.feature.announcement.usecase

class AnnouncementUseCases internal constructor(
    /**
     * 공지 목록 조회
     * @see GetAnnouncementsUseCase
     */
    val getAnnouncements: GetAnnouncementsUseCase,
    /**
     * 공지 읽음 처리
     * @see MarkAnnouncementAsReadUseCase
     */
    val markAnnouncementAsRead: MarkAnnouncementAsReadUseCase,
)
