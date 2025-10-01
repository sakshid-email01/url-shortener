package com.dkb.code.factory.url_shortener.controller

import com.dkb.code.factory.url_shortener.dto.ShortenRequest
import com.dkb.code.factory.url_shortener.exception.UrlNotFoundException
import com.dkb.code.factory.url_shortener.service.UrlService
import com.dkb.code.factory.url_shortener.utils.SuccessResponseBuilder
import com.dkb.code.factory.url_shortener.utils.Validator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/urls")
class UrlController(private val service: UrlService, private val validator: Validator) {

    //APIs return value for happy basic flows, in case of error >> custom GlobalExceptionHandling present
    // response builders are used to generate controller responses
    // error messages stored in separte message.properties file



    // ---------------- POST /urls/short ----------------
    //  basic checks on incoming long url
    //  idempotent creation: if the long URL already has a short URL, return it;
    //  otherwise create one and return the new short URL.
    @PostMapping("/short")
    fun shorten(@RequestBody request: ShortenRequest): ResponseEntity<Any> {
        validator.validateShortenRequest(request)
        val shortenedUrl = service.createShortUrl(request.originalUrl)
        return SuccessResponseBuilder.build(shortenedUrl)
    }



    // ---------------- GET /urls/resolve/{shortKey} ----------------
    // upon passing short key, returns original long url
    @GetMapping("/resolve/{shortKey}")
    fun resolve(@PathVariable shortKey: String): ResponseEntity<Any> {
        val originalUrl = service.resolveShortUrl(shortKey)
        return SuccessResponseBuilder.build(originalUrl)
    }



    // ---------------- GET /urls/{shortKey} ----------------
    // provides direct redirection upon browser hit
    @GetMapping("/{shortKey}")
    fun redirect(@PathVariable shortKey: String): ResponseEntity<Any> {
        val originalUrl = service.resolveShortUrl(shortKey)
        return if (originalUrl != null) {
            ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", originalUrl)
                .build()
        } else {
            throw UrlNotFoundException()
        }


    }

}