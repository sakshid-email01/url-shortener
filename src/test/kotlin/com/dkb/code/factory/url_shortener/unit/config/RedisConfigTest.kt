package com.dkb.code.factory.url_shortener.unit.config

import com.dkb.code.factory.url_shortener.config.AppConfig
import com.dkb.code.factory.url_shortener.config.RedisConfig
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate

class RedisConfigTest {

    private val appConfig = AppConfig(
        redisGlobalCounterKey = "counter",
        redisCounterStartValue = "100",
        redisCounterRadix = "32",
        urlShortenerBasePath = "http://localhost:8080/urls/",
        urlMaxLength = "2048"
    )

    private val redisConfig = RedisConfig(appConfig)

    @Test
    fun `redisTemplate bean should set connectionFactory`() {
        val mockConnectionFactory: RedisConnectionFactory = mockk()

        val template = redisConfig.redisTemplate(mockConnectionFactory)

        assertNotNull(template)
        assertEquals(mockConnectionFactory, template.connectionFactory)
    }

    @Test
    fun `redisService bean should be created`() {
        val mockConnectionFactory: RedisConnectionFactory = mockk()
        val mockTemplate: StringRedisTemplate = mockk()

        val service = redisConfig.redisService(mockConnectionFactory, mockTemplate)

        assertNotNull(service)
        assertTrue(true)
    }
}
