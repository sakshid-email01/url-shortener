package com.dkb.code.factory.url_shortener.exception

import com.dkb.code.factory.url_shortener.utils.ErrorResponseBuilder
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler (private val messageSource: MessageSource){

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    private val locale = LocaleContextHolder.getLocale()


    // UrlNotFoundException
    @ExceptionHandler(UrlNotFoundException::class)
    fun handleUrlNotFound(ex: UrlNotFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.info("Short URL not found: {}", ex.message)
        val message = messageSource.getMessage("error.url_not_found", null,ex.message, locale)
        return ErrorResponseBuilder.build(HttpStatus.NOT_FOUND, message, request)
    }


    // BadRequestException
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val message = messageSource.getMessage("error.invalid_request", null,ex.message, locale)
        return ErrorResponseBuilder.build(HttpStatus.BAD_REQUEST, message, request)
    }


    // Exception
    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("Unexpected error occurred", ex)
        val message = messageSource.getMessage("error.internal", null,ex.message, locale)
        return ErrorResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, message, request)
    }
}
