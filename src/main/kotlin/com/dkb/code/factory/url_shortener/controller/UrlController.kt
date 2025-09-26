package com.dkb.code.factory.url_shortener.controller

import com.dkb.code.factory.url_shortener.entity.UrlEntity
import com.dkb.code.factory.url_shortener.service.UrlService
import org.springframework.web.bind.annotation.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/api/url")
class UrlController(private val service: UrlService) {

    @PostMapping("/short")
    fun shorten(@RequestBody originalUrl: String = ""): UrlEntity {
        return service.createShortUrl(originalUrl)
    }

    @GetMapping("/resolve/{rawShortUrl}")
    fun resolve(@PathVariable raw: String = ""): UrlEntity {
        val decoded = URLDecoder.decode(raw, StandardCharsets.UTF_8.name())
        return service.resolveShortUrl(decoded)
    }

}