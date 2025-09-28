package com.dkb.code.factory.url_shortener.exception

import com.dkb.code.factory.url_shortener.utils.ErrorResponseBuilder
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice(basePackages = ["com.dkb.code.factory.url_shortener.controller"])
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(UrlNotFoundException::class)
    fun handleUrlNotFound(ex: UrlNotFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.info("Short URL not found: {}", ex.message)
        return ErrorResponseBuilder.build(HttpStatus.NOT_FOUND, ex.message, request)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("Unexpected error occurred", ex)
        return ErrorResponseBuilder.build(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An internal error occurred. Please contact support.",
            request
        )
    }
}
