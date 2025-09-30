package com.dkb.code.factory.url_shortener.service

import com.dkb.code.factory.url_shortener.config.AppConfig
import com.dkb.code.factory.url_shortener.entity.UrlEntity
import com.dkb.code.factory.url_shortener.repository.UrlRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service


@Service
class UrlServiceImpl  (private val urlRepository: UrlRepository, private val redisService: RedisService, private val appConfig: AppConfig) : UrlService {

    private val baseUrl = appConfig.urlShortenerBasePath

    // check if the given original url is already shortened
    // if yes, then return its existing short url = baseurl + shortKey
    // if not, then create short url and return new short url = baseurl + shortKey

    override fun createShortUrl(originalUrl: String): String =
        urlRepository.findByOriginalUrl(originalUrl)
            ?.shortUrl
            ?.let { "$baseUrl$it" }
            ?: run {
                val shortKey = redisService.uniqueKey()
                val saved = urlRepository.save(UrlEntity(shortUrl = shortKey, originalUrl = originalUrl))
                "$baseUrl${saved.shortUrl}"
            }


    @Cacheable(
        value = ["\${redis.cache.name:longUrls}"],
        key = "#shortUrl",
        unless = "#result == null" // Do not cache null results
    )
    override fun resolveShortUrl(shortUrl: String): String? {
        return urlRepository.findById(shortUrl)
            .orElse(null)
            ?.originalUrl
    }

}

