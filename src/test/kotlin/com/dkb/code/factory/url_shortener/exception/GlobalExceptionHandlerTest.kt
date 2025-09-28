package com.dkb.code.factory.url_shortener.exception

import com.dkb.code.factory.url_shortener.utils.ErrorResponseBuilder
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import jakarta.servlet.http.HttpServletRequest

class GlobalExceptionHandlerTest {

    private val messageSource: MessageSource = mockk()
    private val request: HttpServletRequest = mockk()
    private val handler = GlobalExceptionHandler(messageSource)

    @Test
    fun `handleUrlNotFound returns 404 response`() {
        val ex = UrlNotFoundException("Not found")
        every { messageSource.getMessage("error.url_not_found", null, ex.message, any()) } returns "URL not found"
        every { request.requestURI } returns "/urls/missing"

        val response = handler.handleUrlNotFound(ex, request)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("URL not found", response.body?.message)
    }

    @Test
    fun `handleBadRequest returns 400 response`() {
        val ex = BadRequestException("error.bad_request")
        every { messageSource.getMessage(ex.message, null, ex.message, any()) } returns "Bad request"
        every { request.requestURI } returns "/urls/short"

        val response = handler.handleBadRequest(ex, request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Bad request", response.body?.message)
    }

    @Test
    fun `handleUnexpected returns 500 response`() {
        val ex = RuntimeException("unexpected error")
        every { messageSource.getMessage("error.internal", null, ex.message, any()) } returns "Internal error"
        every { request.requestURI } returns "/urls/any"

        val response = handler.handleUnexpected(ex, request)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("Internal error", response.body?.message)
    }
}
