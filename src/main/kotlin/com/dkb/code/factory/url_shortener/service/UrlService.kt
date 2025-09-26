package com.dkb.code.factory.url_shortener.service

import com.dkb.code.factory.url_shortener.entity.UrlEntity
import java.util.Optional

interface UrlService {
    fun createShortUrl(originalUrl : String): UrlEntity
    fun resolveShortUrl(shortUrl : String): UrlEntity
}