package com.dkb.code.factory.url_shortener.unit.utils

import com.dkb.code.factory.url_shortener.dto.ShortenRequest
import com.dkb.code.factory.url_shortener.exception.BadRequestException
import com.dkb.code.factory.url_shortener.unit.config.GlobalConfigTest
import com.dkb.code.factory.url_shortener.utils.Validator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ValidatorTest : GlobalConfigTest() {

    private val validator = Validator(config)

    @Test
    fun `empty request throws error`() {
        val ex = assertThrows(BadRequestException::class.java) {
            validator.validateShortenRequest(ShortenRequest(""))
        }
        assertEquals("error.missing_original_url", ex.message)
    }

    @Test
    fun `null originalUrl throws error`() {
        val ex = assertThrows(BadRequestException::class.java) {
            validator.validateShortenRequest(ShortenRequest(originalUrl = ""))
        }
        assertEquals("error.missing_original_url", ex.message)
    }

    @Test
    fun `url too long throws error`() {
        val longUrl = "a".repeat(2500) // > urlMaxLength (from test.properties)
        val ex = assertThrows(BadRequestException::class.java) {
            validator.validateShortenRequest(ShortenRequest(longUrl))
        }
        assertEquals("error.url_too_long", ex.message)
    }

    @Test
    fun `blocked domain throws error`() {
        val url = "http://blocked-domain.com/page"
        val ex = assertThrows(BadRequestException::class.java) {
            validator.validateShortenRequest(ShortenRequest(url))
        }
        assertEquals("error.blocked_domain", ex.message)
    }

    @Test
    fun `valid url passes validation`() {
        val url = "http://example.com"
        assertDoesNotThrow {
            validator.validateShortenRequest(ShortenRequest(url))
        }
    }
}
