package com.dkb.code.factory.url_shortener.unit.exception

import com.dkb.code.factory.url_shortener.exception.BadRequestException
import com.dkb.code.factory.url_shortener.exception.GlobalExceptionHandler
import com.dkb.code.factory.url_shortener.exception.UrlNotFoundException
import com.dkb.code.factory.url_shortener.exception.ErrorResponse
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class GlobalExceptionHandlerTest {

    private val messageSource: MessageSource = mockk()
    private val request: HttpServletRequest = mockk()
    private val handler = GlobalExceptionHandler(messageSource)

    @Test
    fun `handleUrlNotFound should return 404`() {
        every { messageSource.getMessage("error.url_not_found", null, any(), any()) } returns "url not found"
        every { request.requestURI } returns "/urls/abc"

        val response = handler.handleUrlNotFound(UrlNotFoundException("not found"), request)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertTrue(response.body is ErrorResponse)
        assertEquals("url not found", (response.body as ErrorResponse).message)
    }

    @Test
    fun `handleBadRequest should return 400`() {
        every { messageSource.getMessage("error.invalid_request", null, any(), any()) } returns "invalid request"
        every { request.requestURI } returns "/urls/short"

        val response = handler.handleBadRequest(BadRequestException("bad request"), request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertTrue(response.body is ErrorResponse)
        assertEquals("invalid request", (response.body as ErrorResponse).message)
    }

    @Test
    fun `handleUnexpected should return 500`() {
        every { messageSource.getMessage("error.internal", null, any(), any()) } returns "internal error"
        every { request.requestURI } returns "/urls/error"

        val response = handler.handleUnexpected(RuntimeException("unexpected"), request)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertTrue(response.body is ErrorResponse)
        assertEquals("internal error", (response.body as ErrorResponse).message)
    }
}
