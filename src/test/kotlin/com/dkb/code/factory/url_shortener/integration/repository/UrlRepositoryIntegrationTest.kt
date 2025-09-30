package com.dkb.code.factory.url_shortener.integration.repository

import com.dkb.code.factory.url_shortener.entity.UrlEntity
import com.dkb.code.factory.url_shortener.repository.UrlRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase

@DataJpaTest
// Let Spring replace the DataSource with an embedded DB (H2) for tests
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UrlRepositoryIntegrationTest {

    @Autowired
    lateinit var repository: UrlRepository

    @Test
    fun `save and find by shortUrl should work`() {
        val entity = UrlEntity(shortUrl = "abc123", originalUrl = "https://example.com/1")

        val saved = repository.save(entity)
        assertEquals("abc123", saved.shortUrl)

        val fetched = repository.findById("abc123").orElse(null)
        assertNotNull(fetched)
        assertEquals("https://example.com/1", fetched!!.originalUrl)
    }

    @Test
    fun `findByOriginalUrl should return entity when exists`() {
        val entity = UrlEntity(shortUrl = "xyz789", originalUrl = "https://example.com/find-me")
        repository.save(entity)

        val found = repository.findByOriginalUrl("https://example.com/find-me")
        assertNotNull(found)
        assertEquals("xyz789", found!!.shortUrl)
    }

    @Test
    fun `delete by shortUrl should remove entity`() {
        val entity = UrlEntity(shortUrl = "del123", originalUrl = "https://example.com/delete-me")
        repository.save(entity)

        assertTrue(repository.existsById("del123"))
        repository.deleteById("del123")
        assertFalse(repository.existsById("del123"))
    }
}
