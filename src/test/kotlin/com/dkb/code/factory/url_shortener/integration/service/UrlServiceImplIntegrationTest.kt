package com.dkb.code.factory.url_shortener.integration.service

import com.dkb.code.factory.url_shortener.entity.UrlEntity
import com.dkb.code.factory.url_shortener.repository.UrlRepository
import com.dkb.code.factory.url_shortener.service.RedisService
import com.dkb.code.factory.url_shortener.service.UrlService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // use H2
@Transactional // rollback after each test
class UrlServiceImplIntegrationTest @Autowired constructor (
       val urlService: UrlService,
       val urlRepository: UrlRepository,
) {

    @Test
    fun `createShortUrl should save new entity when not exists`() {
        val originalUrl = "https://newsite.com"
        val shortUrl = urlService.createShortUrl(originalUrl)

        // Service returns a full short URL
        assertTrue(shortUrl.contains("http://localhost:8080/urls/"))

        // Repository should now have the entity
        val saved = urlRepository.findByOriginalUrl(originalUrl)
        assertNotNull(saved)
        assertEquals(originalUrl, saved!!.originalUrl)
    }

    @Test
    fun `createShortUrl should return existing mapping if original already exists`() {
        val entity = UrlEntity(shortUrl = "abc123", originalUrl = "https://example.com")
        urlRepository.save(entity)

        val result = urlService.createShortUrl("https://example.com")

        assertEquals("http://localhost:8080/urls/abc123", result)
    }

    @Test
    fun `resolveShortUrl should return originalUrl if found`() {
        val entity = UrlEntity(shortUrl = "xyz789", originalUrl = "https://mysite.com")
        urlRepository.save(entity)

        val result = urlService.resolveShortUrl("xyz789")

        assertEquals("https://mysite.com", result)
    }

    @Test
    fun `resolveShortUrl should return null if not found`() {
        val result = urlService.resolveShortUrl("missingKey")
        assertNull(result)
    }
}
