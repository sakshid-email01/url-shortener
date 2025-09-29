package com.dkb.code.factory.url_shortener.unit.config

import com.dkb.code.factory.url_shortener.config.AppConfig

open class GlobalConfigTest {

    protected val appConfig = AppConfig(
        redisGlobalCounterKey = "counter",
        redisCounterStartValue = "100",
        redisCounterRadix = "32",
        urlShortenerBasePath = "http://localhost:8080/urls/",
        urlMaxLength = "2048"
    )


}
