package com.dkb.code.factory.url_shortener.service

import com.dkb.code.factory.url_shortener.config.GlobalConfig
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.support.atomic.RedisAtomicLong

/**
 * Service for managing global counters and storing URL mappings in Redis.
 */
class RedisService(
    private val connectionFactory: RedisConnectionFactory,
    private val redisTemplate: StringRedisTemplate,
    private val globalConfig: GlobalConfig
) {

    /**
     * Generate a globally unique key as a base-32 string.
     * Uses RedisAtomicLong to maintain a distributed counter.
     */
    fun uniqueKey(): String {
        val atomicLong = RedisAtomicLong(globalConfig.redisGlobalCounterKey, connectionFactory)
        var currentId = atomicLong.incrementAndGet()

        // Initialize counter to START_KEY if this is the first increment
        if (currentId == 1L) {
            atomicLong.set(globalConfig.redisCounterStartValue.toLong())
            currentId = globalConfig.redisCounterStartValue.toLong()
        }

        // Return the ID as a base-32 string for short representation
        return currentId.toString(globalConfig.redisCounterRadix.toInt())
    }

    /**
     * Write a string value to Redis (no expiry).
     */
    fun writeValue(key: String, value: String) {
        redisTemplate.opsForValue().set(key, value)
    }

    /**
     * Read a string value from Redis.
     */
    fun readValue(key: String): String? {
        return redisTemplate.opsForValue().get(key)
    }
}