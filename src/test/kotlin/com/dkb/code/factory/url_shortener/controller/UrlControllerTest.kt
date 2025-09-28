package com.dkb.code.factory.url_shortener.controller

import com.dkb.code.factory.url_shortener.exception.UrlNotFoundException
import com.dkb.code.factory.url_shortener.service.UrlService
import com.dkb.code.factory.url_shortener.utils.Validator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import com.dkb.code.factory.url_shortener.controller.UrlController

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
        val requestJson: ObjectNode = objectMapper.createObjectNode()
        requestJson.put("originalUrl", "http://example.com")

        every { validator.validateShortenRequest(any()) } just Runs
        every { service.createShortUrl("http://example.com") } returns "http://short.ly/abc123"

        mockMvc.perform(
            post("/urls/short")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().string("http://short.ly/abc123"))

        verify { validator.validateShortenRequest(any()) }
        verify { service.createShortUrl("http://example.com") }
    }

    @Test
    fun `resolve returns 200 with original url`() {
        every { service.resolveShortUrl("abc123") } returns "http://example.com"

        mockMvc.perform(get("/urls/resolve/abc123"))
            .andExpect(status().isOk)
            .andExpect(content().string("http://example.com"))

        verify { service.resolveShortUrl("abc123") }
    }



    @Test
    fun `redirect returns 302 when url found`() {
        every { service.resolveShortUrl("abc123") } returns "http://example.com"

        mockMvc.perform(get("/urls/abc123"))
            .andExpect(status().isFound)
            .andExpect(header().string("Location", "http://example.com"))

        verify { service.resolveShortUrl("abc123") }
    }

    @Test
    fun `redirect throws UrlNotFoundException when url not found`() {
        // given
        every { service.resolveShortUrl("missingKey") } returns null

        // when + then
        assertThrows(UrlNotFoundException::class.java) {
            urlController.redirect("missingKey")
        }
    }

    @Test
    fun `resolve throws UrlNotFoundException when url not found`() {
        // given
        every { service.resolveShortUrl("missingKey") } returns null

        // when + then
        assertThrows(UrlNotFoundException::class.java) {
            urlController.resolve("missingKey")
        }
    }




}
