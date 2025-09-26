package com.dkb.code.factory.url_shortener.config

import com.dkb.code.factory.url_shortener.service.RedisService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate


@Configuration
class RedisConfig {

    companion object {
        const val ID_KEY = "all:urls:unique_id:key"
        const val KEY_RADIX = 32
        const val START_KEY = 100_000L
    }

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): StringRedisTemplate {
        val template = StringRedisTemplate()
        template.connectionFactory = connectionFactory
        return template
    }

    @Bean
    fun redisService(
        connectionFactory: RedisConnectionFactory,
        redisTemplate: StringRedisTemplate
    ): RedisService {
        return RedisService(connectionFactory, redisTemplate)
    }
}
