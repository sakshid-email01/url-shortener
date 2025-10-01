package com.dkb.code.factory.url_shortener.service

import com.dkb.code.factory.url_shortener.config.GlobalConfig
import com.dkb.code.factory.url_shortener.entity.UrlEntity
import com.dkb.code.factory.url_shortener.repository.UrlRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class UrlServiceImpl  (private val urlRepository: UrlRepository, private val redisService: RedisService, private val globalConfig: GlobalConfig) : UrlService {

    private val baseUrl = globalConfig.urlShortenerBasePath

    //idempotent creation: if the long URL already has a short URL, return it;
    //otherwise create one and return the new short URL
    @Transactional
    override fun createShortUrl(originalUrl: String): String {
        val existing = urlRepository.findByOriginalUrl(originalUrl)
        if (existing != null) {
            return "$baseUrl${existing.shortUrl}"
        } else {
            val shortKey = redisService.uniqueKey()
            val saved = urlRepository.save(UrlEntity(shortUrl = shortKey, originalUrl = originalUrl))
            return "$baseUrl${saved.shortUrl}"
        }
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

