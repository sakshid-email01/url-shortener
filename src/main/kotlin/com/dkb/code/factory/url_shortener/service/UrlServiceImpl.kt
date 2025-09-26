package com.dkb.code.factory.url_shortener.service

import com.dkb.code.factory.url_shortener.entity.UrlEntity
import com.dkb.code.factory.url_shortener.repository.UrlRepository
import org.springframework.stereotype.Service


@Service
class UrlServiceImpl  (private val urlRepository: UrlRepository, private val redisService: RedisService) : UrlService {

    private val baseUrl = "http://dkb/cf/url-shortener/"

    override fun createShortUrl(originalUrl: String): String =
        urlRepository.findByOriginalUrl(originalUrl)
            ?.shortUrl  // check if the given original url is already shortened
            ?.let { "$baseUrl$it" } // if yes, then return its existing short url = shortKey + baseurl
            ?: run { // if not, then create short url and return new short url = shortKey + baseurl
                val shortKey = redisService.uniqueKey()
                val saved = urlRepository.save(UrlEntity(shortUrl = shortKey, originalUrl = originalUrl))
                "$baseUrl${saved.shortUrl}"
            }


    override fun resolveShortUrl(shortUrl: String): String? {
        println(shortUrl)
        val originalUrl = urlRepository.findById(shortUrl)
            .orElse(null)
            ?.originalUrl
        println(originalUrl)
        return originalUrl
    }

}

