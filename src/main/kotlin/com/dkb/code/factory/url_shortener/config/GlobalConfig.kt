package com.dkb.code.factory.url_shortener.config//package com.dkb.code.factory.url_shortener.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "app")
data class GlobalConfig(
    val redisGlobalCounterKey: String,
    val redisCounterStartValue: String,
    val redisCounterRadix: String,
    val urlShortenerBasePath: String,
    val urlMaxLength: String
)

