package com.dkb.code.factory.url_shortener.unit.controller

import com.dkb.code.factory.url_shortener.controller.UrlController
import com.dkb.code.factory.url_shortener.dto.ShortenRequest
import com.dkb.code.factory.url_shortener.exception.UrlNotFoundException
import com.dkb.code.factory.url_shortener.service.UrlService
import com.dkb.code.factory.url_shortener.utils.Validator
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class UrlControllerTest {

    private val service: UrlService = mockk()
    private val validator: Validator = mockk()
    private lateinit var mockMvc: MockMvc
    private val objectMapper = ObjectMapper()
    private val urlController: UrlController = UrlController(service, validator)

    @BeforeEach
    fun setup() {
        val controller = UrlController(service, validator)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `shorten returns 200 with short url`() {
        val request = ShortenRequest("http://example.com")
        val requestJson = objectMapper.writeValueAsString(request)

        every { validator.validateShortenRequest(any()) } just Runs
        every { service.createShortUrl("http://example.com") } returns "http://short.ly/abc123"

        mockMvc.perform(
            MockMvcRequestBuilders.post("/urls/short")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("http://short.ly/abc123"))

        verify { validator.validateShortenRequest(any()) }
        verify { service.createShortUrl("http://example.com") }
    }

    @Test
    fun `resolve returns 200 with original url`() {
        every { service.resolveShortUrl("abc123") } returns "http://example.com"

        mockMvc.perform(MockMvcRequestBuilders.get("/urls/resolve/abc123"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("http://example.com"))


        verify { service.resolveShortUrl("abc123") }
    }

    @Test
    fun `redirect returns 302 when url found`() {
        every { service.resolveShortUrl("abc123") } returns "http://example.com"

        mockMvc.perform(MockMvcRequestBuilders.get("/urls/abc123"))
            .andExpect(MockMvcResultMatchers.status().isFound)
            .andExpect(MockMvcResultMatchers.header().string("Location", "http://example.com"))

        verify { service.resolveShortUrl("abc123") }
    }

    @Test
    fun `redirect throws UrlNotFoundException when url not found`() {
        every { service.resolveShortUrl("missingKey") } returns null

        Assertions.assertThrows(UrlNotFoundException::class.java) {
            urlController.redirect("missingKey")
        }
    }

    @Test
    fun `resolve throws UrlNotFoundException when url not found`() {
        every { service.resolveShortUrl("missingKey") } returns null

        Assertions.assertThrows(UrlNotFoundException::class.java) {
            urlController.resolve("missingKey")
        }
    }
}