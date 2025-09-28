package com.dkb.code.factory.url_shortener.utils

import com.dkb.code.factory.url_shortener.config.AppConfig
import com.dkb.code.factory.url_shortener.exception.BadRequestException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ValidatorTest {

    private val appConfig = AppConfig(
        redisGlobalCounterKey = "counter",
        redisCounterStartValue = "100",
        redisCounterRadix = "32",
        urlShortenerBasePath = "http://localhost:8080/urls/",
        urlMaxLength = "2048"
    )

    private val validator = Validator(appConfig)
    private val mapper = ObjectMapper()

    @Test
    fun `empty request throws error`() {
        val emptyNode: ObjectNode = mapper.createObjectNode()
        val ex = assertThrows(BadRequestException::class.java) {
            validator.validateShortenRequest(emptyNode)
        }
        assertEquals("error.empty_request", ex.message)
    }

    @Test
    fun `missing originalUrl throws error`() {
        val node = mapper.createObjectNode().put("foo", "bar")
        val ex = assertThrows(BadRequestException::class.java) {
            validator.validateShortenRequest(node)
        }
        assertEquals("error.missing_original_url", ex.message)
    }

    @Test
    fun `blank originalUrl throws error`() {
        val node = mapper.createObjectNode().put("originalUrl", "")
        val ex = assertThrows(BadRequestException::class.java) {
            validator.validateShortenRequest(node)
        }
        assertEquals("error.missing_original_url", ex.message)
    }

    @Test
    fun `url too long throws error`() {
        val longUrl = "a".repeat(2500) // > urlMaxLength (20)
        val node = mapper.createObjectNode().put("originalUrl", longUrl)
        val ex = assertThrows(BadRequestException::class.java) {
            validator.validateShortenRequest(node)
        }
        assertEquals("error.url_too_long", ex.message)
    }

    @Test
    fun `blocked domain throws error`() {
        val url = "http://blocked-domain.com/page"
        val node = mapper.createObjectNode().put("originalUrl", url)
        val ex = assertThrows(BadRequestException::class.java) {
            validator.validateShortenRequest(node)
        }
        assertEquals("error.blocked_domain", ex.message)
    }

    @Test
    fun `valid url passes validation`() {
        val url = "http://example.com"
        val node = mapper.createObjectNode().put("originalUrl", url)
        assertDoesNotThrow {
            validator.validateShortenRequest(node)
        }
    }
}
