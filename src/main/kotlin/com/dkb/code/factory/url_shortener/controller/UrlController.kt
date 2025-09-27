package com.dkb.code.factory.url_shortener.controller

import com.dkb.code.factory.url_shortener.service.UrlService
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/urls")
class UrlController(private val service: UrlService) {

    @PostMapping("/short")
    fun shorten(@RequestBody request: JsonNode): ResponseEntity<String> {
        val originalUrl = request.get("originalUrl").asText()
        val resp = service.createShortUrl(originalUrl)
        return ResponseEntity.ok(resp)  // 200 OK,
    }


    @GetMapping("/resolve/{shortKey}")
    fun resolve(@PathVariable shortKey: String = ""): ResponseEntity<String> {
        val resp = service.resolveShortUrl(shortKey) ?: "No URL mapping found"
        return ResponseEntity.ok(resp)
    }

    @GetMapping("/{shortKey}")
    fun redirect(@PathVariable shortKey: String): ResponseEntity<Any> {
        val originalUrl = service.resolveShortUrl(shortKey)
        return if (originalUrl != null) {
            ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", originalUrl)
                .build()
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short URL not found")
        }
    }

}