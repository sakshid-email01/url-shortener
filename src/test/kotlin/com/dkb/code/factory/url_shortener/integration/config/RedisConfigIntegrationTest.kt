package com.dkb.code.factory.url_shortener.integration

import com.dkb.code.factory.url_shortener.config.AppConfig
import com.dkb.code.factory.url_shortener.config.RedisConfig
import com.dkb.code.factory.url_shortener.service.RedisService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.core.StringRedisTemplate
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedisConfigIntegrationTest {

    companion object {
        @Container
        val redis: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:7.0-alpine"))
            .withExposedPorts(6379)
    }

    private fun connectionFactory(): LettuceConnectionFactory {
        val config = RedisStandaloneConfiguration(redis.host, redis.firstMappedPort)
        return LettuceConnectionFactory(config).apply { afterPropertiesSet() }
    }

    private fun appConfig() = AppConfig(
        redisGlobalCounterKey = "test:counter",
        redisCounterStartValue = "100",
        redisCounterRadix = "32",
        urlShortenerBasePath = "http://localhost:8080/urls/",
        urlMaxLength = "2048"
    )

    @Test
    fun `redisTemplate should connect and set values`() {
        val config = RedisConfig(appConfig())
        val factory = connectionFactory()
        val template: StringRedisTemplate = config.redisTemplate(factory).apply { afterPropertiesSet() }

        template.opsForValue().set("hello", "world")
        val result = template.opsForValue().get("hello")

        assertEquals("world", result)
    }

    @Test
    fun `redisService should generate keys and read write values`() {
        val config = RedisConfig(appConfig())
        val factory = connectionFactory()
        val template = config.redisTemplate(factory).apply { afterPropertiesSet() }

        val service: RedisService = config.redisService(factory, template)

        // Test counter generation
        val key1 = service.uniqueKey()
        val key2 = service.uniqueKey()
        assertNotEquals(key1, key2)

        // Test write + read
        service.writeValue("short:test:1", "https://example.com")
            val result = service.readValue("short:test:1")

        assertEquals("https://example.com", result)
    }
}
