package com.dkb.code.factory.url_shortener.service

import com.dkb.code.factory.url_shortener.entity.UrlEntity
import com.dkb.code.factory.url_shortener.repository.UrlRepository
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class UrlServiceImpl  (private val urlRepository: UrlRepository, private val redisService: RedisService) : UrlService {

    private val baseUrl = "http://custom-url-shortener/"

    override fun createShortUrl(originalUrl: String): UrlEntity {
        val shortKey = redisService.uniqueKey()
        redisService.writeValue(shortKey, originalUrl)
        val url = UrlEntity(shortUrl = "$baseUrl$shortKey", originalUrl = originalUrl)
        val response = urlRepository.save(url)
        return response
    }


    override fun resolveShortUrl(shortUrl: String): UrlEntity {
        return urlRepository.findById(shortUrl)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "No URL mapping found for $shortUrl") }
    }
}



