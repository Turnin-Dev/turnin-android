package com.turnin.shared.core.network

internal enum class HttpStatusCode(val code: Int) {
    BadRequest(400),
    Unauthorized(401),
    Forbidden(403),
    NotFound(404),
    RequestTimeout(408),
    Conflict(409),
    InternalServerError(500),
    BadGateway(502),
    ServiceUnavailable(503),
    GatewayTimeout(504),
}
