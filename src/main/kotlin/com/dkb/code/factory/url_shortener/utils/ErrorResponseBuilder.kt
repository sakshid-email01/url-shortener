package com.dkb.code.factory.url_shortener.utils

import com.dkb.code.factory.url_shortener.exception.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

object ErrorResponseBuilder {

    /**
     * Builds a standard ErrorResponse wrapped in ResponseEntity.
     */
    fun build(status: HttpStatus, message: String?, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = message,
            path = request.requestURI
        )
        return ResponseEntity(body, status)
    }
}
