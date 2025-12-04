package com.peekr.core.data

enum class HttpStatusCode(val code: Int) {
    NoContent(204),
    BadRequest(400),
    Unauthorized(401),
    Forbidden(403),
    NotFound(404),
    Conflict(409),
    RequestTimeout(408),
    InternalServerError(500),
    BadGateway(502),
    ServiceUnavailable(503),
    GatewayTimeout(504),
}
