package com.dkb.code.factory.url_shortener.unit.service

import com.dkb.code.factory.url_shortener.service.RedisService
import com.dkb.code.factory.url_shortener.unit.config.GlobalConfigTest
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.data.redis.support.atomic.RedisAtomicLong

class RedisServiceTest : GlobalConfigTest()  {

    private val connectionFactory: RedisConnectionFactory = mockk()
    private val redisTemplate: StringRedisTemplate = mockk()
    private val valueOps: ValueOperations<String, String> = mockk()
    private val redisService = RedisService(connectionFactory, redisTemplate, config)

    @Test
    fun `write and read value`() {
        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.set("foo", "bar") } just Runs
        every { valueOps.get("foo") } returns "bar"

        redisService.writeValue("foo", "bar")
        val result = redisService.readValue("foo")

        assertEquals("bar", result)
        verify { valueOps.set("foo", "bar") }
        verify { valueOps.get("foo") }
    }

    @Test
    fun `uniqueKey generates base-32 string`() {
        mockkConstructor(RedisAtomicLong::class)

        // mock RedisAtomicLong internals
        every { anyConstructed<RedisAtomicLong>().incrementAndGet() } returns 123L
        every { anyConstructed<RedisAtomicLong>().set(any()) } just Runs

        // also mock RedisConnectionFactory.getConnection() since RedisAtomicLong calls it
        every { connectionFactory.connection } returns mockk(relaxed = true)

        val result = redisService.uniqueKey()

        // 123 in base-32 = "3r"
        assertEquals("3r", result)
        verify { anyConstructed<RedisAtomicLong>().incrementAndGet() }
    }

    @Test
    fun `uniqueKey initializes counter when first increment is 1`() {
        mockkConstructor(RedisAtomicLong::class)

        // mock RedisAtomicLong internals
        every { anyConstructed<RedisAtomicLong>().incrementAndGet() } returns 1L
        every { anyConstructed<RedisAtomicLong>().set(config.redisCounterStartValue.toLong()) } just Runs

        // mock RedisConnectionFactory call
        every { connectionFactory.connection } returns mockk(relaxed = true)

        val result = redisService.uniqueKey()

        // redisCounterStartValue is 100, in base-32 that is "34"
        assertEquals("34", result)

        verify { anyConstructed<RedisAtomicLong>().set(config.redisCounterStartValue.toLong()) }
    }


}
