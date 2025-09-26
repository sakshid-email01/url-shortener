package com.dkb.code.factory.url_shortener.service

import com.dkb.code.factory.url_shortener.entity.UrlEntity
import com.dkb.code.factory.url_shortener.repository.UrlRepository
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class UrlServiceImpl  (private val urlRepository: UrlRepository) : UrlService {

    override fun createShortUrl(originalUrl: String): UrlEntity {
        val url = UrlEntity(shortUrl = "{test}", originalUrl = originalUrl)
        return urlRepository.save(url)
    }


    override fun resolveShortUrl(shortUrl: String): UrlEntity {
        return urlRepository.findById(shortUrl)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "No URL mapping found for $shortUrl") }
    }
}



