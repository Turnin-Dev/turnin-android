import Shared

extension SharedEntryPoint {
    /// KMP 공유 객체(Facade)를 타입으로 꺼낸다.
    ///
    /// ```swift
    /// let announcements: AnnouncementFacade = SharedEntryPoint.inject()
    /// ```
    static func inject<T: AnyObject>() -> T {
        // swiftlint:disable:next force_cast
        shared.get(objCClass: T.self) as! T
    }
}
