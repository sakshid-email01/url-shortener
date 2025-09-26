package com.dkb.code.factory.url_shortener.service


interface UrlService {
    fun createShortUrl(originalUrl : String): String
    fun resolveShortUrl(shortUrl : String): String?
}