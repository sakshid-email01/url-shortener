package com.dkb.code.factory.url_shortener.unit.service

import com.dkb.code.factory.url_shortener.entity.UrlEntity
import com.dkb.code.factory.url_shortener.repository.UrlRepository
import com.dkb.code.factory.url_shortener.service.RedisService
import com.dkb.code.factory.url_shortener.service.UrlServiceImpl
import com.dkb.code.factory.url_shortener.unit.config.GlobalConfigTest
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class UrlServiceImplTest : GlobalConfigTest() {

    private val urlRepository: UrlRepository = mockk()
    private val redisService: RedisService = mockk()
    private val urlService = UrlServiceImpl(urlRepository, redisService, config)

    @Test
    fun `createShortUrl returns existing short url if original already stored`() {
        val originalUrl = "https://example.com"
        val entity = UrlEntity(shortUrl = "abc123", originalUrl = originalUrl)

        every { urlRepository.findByOriginalUrl(originalUrl) } returns entity

        val result = urlService.createShortUrl(originalUrl)

        assertEquals("http://localhost:8080/urls/abc123", result)
        verify(exactly = 1) { urlRepository.findByOriginalUrl(originalUrl) }
        verify(exactly = 0) { redisService.uniqueKey() }
        verify(exactly = 0) { urlRepository.save(any()) }
    }

    @Test
    fun `createShortUrl generates new short url if original not stored`() {
        val originalUrl = "https://newsite.com"
        val generatedKey = "xyz789"
        val savedEntity = UrlEntity(shortUrl = generatedKey, originalUrl = originalUrl)

        every { urlRepository.findByOriginalUrl(originalUrl) } returns null
        every { redisService.uniqueKey() } returns generatedKey
        every { urlRepository.save(any()) } returns savedEntity

        val result = urlService.createShortUrl(originalUrl)

        assertEquals("http://localhost:8080/urls/xyz789", result)
        verify { urlRepository.findByOriginalUrl(originalUrl) }
        verify { redisService.uniqueKey() }
        verify { urlRepository.save(match { it.originalUrl == originalUrl && it.shortUrl == generatedKey }) }
    }

    @Test
    fun `resolveShortUrl returns original url when found`() {
        val shortKey = "abc123"
        val entity = UrlEntity(shortUrl = shortKey, originalUrl = "https://example.com")

        every { urlRepository.findById(shortKey) } returns Optional.of(entity)

        val result = urlService.resolveShortUrl(shortKey)

        assertEquals("https://example.com", result)
        verify { urlRepository.findById(shortKey) }
    }

    @Test
    fun `resolveShortUrl returns null when not found`() {
        val shortKey = "missingKey"

        every { urlRepository.findById(shortKey) } returns Optional.empty()

        val result = urlService.resolveShortUrl(shortKey)

        assertNull(result)
        verify { urlRepository.findById(shortKey) }
    }
}
