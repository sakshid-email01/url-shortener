package com.dkb.code.factory.url_shortener.integration.controller

import com.dkb.code.factory.url_shortener.controller.UrlController
import com.dkb.code.factory.url_shortener.service.UrlService
import com.dkb.code.factory.url_shortener.utils.Validator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.test.context.bean.override.mockito.MockitoBean

@WebMvcTest(controllers = [UrlController::class])
class UrlControllerIntegrationTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper
) {

    @MockitoBean
    lateinit var service: UrlService

    @MockitoBean
    lateinit var validator: Validator

    @Test
    fun `shorten should return 200 with short url`() {
        val request = mapOf("originalUrl" to "https://example.com")

        whenever(service.createShortUrl("https://example.com"))
            .thenReturn("http://localhost:8080/urls/abc123")

        mockMvc.perform(
            post("/urls/short")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().string("http://localhost:8080/urls/abc123"))

        verify(validator).validateShortenRequest(any<JsonNode>())
        verify(service).createShortUrl("https://example.com")
    }

    @Test
    fun `resolve should return 200 when url exists`() {
        whenever(service.resolveShortUrl("abc123"))
            .thenReturn("https://example.com")

        mockMvc.perform(get("/urls/resolve/abc123"))
            .andExpect(status().isOk)
            .andExpect(content().string("https://example.com"))

        verify(service).resolveShortUrl("abc123")
    }

    @Test
    fun `resolve should return 404 when url not found`() {
        whenever(service.resolveShortUrl("missing"))
            .thenReturn(null)

        mockMvc.perform(get("/urls/resolve/missing"))
            .andExpect(status().isNotFound)

        verify(service).resolveShortUrl("missing")
    }

    @Test
    fun `redirect should return 302 when url exists`() {
        whenever(service.resolveShortUrl("abc123"))
            .thenReturn("https://example.com")

        mockMvc.perform(get("/urls/abc123"))
            .andExpect(status().isFound)
            .andExpect(header().string("Location", "https://example.com"))

        verify(service).resolveShortUrl("abc123")
    }

    @Test
    fun `redirect should return 404 when url not found`() {
        whenever(service.resolveShortUrl("missing"))
            .thenReturn(null)

        mockMvc.perform(get("/urls/missing"))
            .andExpect(status().isNotFound)

        verify(service).resolveShortUrl("missing")
    }
}
