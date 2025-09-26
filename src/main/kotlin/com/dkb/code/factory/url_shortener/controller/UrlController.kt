package com.dkb.code.factory.url_shortener.controller

import com.dkb.code.factory.url_shortener.entity.UrlEntity
import com.dkb.code.factory.url_shortener.service.UrlService
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/api/url")
class UrlController(private val service: UrlService) {

    @PostMapping("/short")
    fun shorten(@RequestBody request: JsonNode): ResponseEntity<Void> {
        val originalUrl = request.get("originalUrl").asText()
        service.createShortUrl(originalUrl)
        return ResponseEntity.ok().build()  // 200 OK, no body
    }


    @GetMapping("/resolve/{rawShortUrl}")
    fun resolve(@PathVariable raw: String = ""): UrlEntity {
        val decoded = URLDecoder.decode(raw, StandardCharsets.UTF_8.name())
        return service.resolveShortUrl(decoded)
    }

}