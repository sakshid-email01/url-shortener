package com.dkb.code.factory.url_shortener.integration.controller

import com.dkb.code.factory.url_shortener.dto.ShortenRequest
import com.dkb.code.factory.url_shortener.exception.UrlNotFoundException
import com.dkb.code.factory.url_shortener.service.UrlService
import com.dkb.code.factory.url_shortener.utils.Validator
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.mockito.BDDMockito.*
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest
@AutoConfigureMockMvc
class UrlControllerIntegrationTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper
) {

    @MockitoBean
    lateinit var urlService: UrlService

    @MockitoBean
    lateinit var validator: Validator

    @Test
    fun `POST shorten should return shortened url`() {
        val request = ShortenRequest("https://example.com")
        given(urlService.createShortUrl(request.originalUrl)).willReturn("http://localhost:8080/abc123")

        mockMvc.post("/urls/short") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isOk() }
            }

        verify(validator).validateShortenRequest(request)
        verify(urlService).createShortUrl("https://example.com")
    }

    @Test
    fun `GET resolve should return original url`() {
        given(urlService.resolveShortUrl("abc123")).willReturn("https://example.com")

        val mvcResult = mockMvc.get("/urls/resolve/abc123")
            .andExpect {
                status { isOk() }
            }
            .andReturn()

        assertEquals(true, mvcResult.response.contentAsString.contains("https://example.com"))
        verify(urlService).resolveShortUrl("abc123")
    }

    @Test
    fun `GET redirect should return 302 when url exists`() {
        given(urlService.resolveShortUrl("abc123")).willReturn("https://redirect.com")

        val mvcResult = mockMvc.get("/urls/abc123")
            .andExpect {
                status { isFound() }
            }
            .andReturn()

        assertEquals("https://redirect.com", mvcResult.response.getHeader("Location"))
        verify(urlService).resolveShortUrl("abc123")
    }

    @Test
    fun `GET redirect should throw UrlNotFoundException when url does not exist`() {
        given(urlService.resolveShortUrl("missing")).willReturn(null)

        mockMvc.get("/urls/missing")
            .andExpect {
                status { isNotFound() }
            }

        verify(urlService).resolveShortUrl("missing")
    }
}
