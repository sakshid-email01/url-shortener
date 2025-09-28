package com.dkb.code.factory.url_shortener.utils

import com.dkb.code.factory.url_shortener.config.AppConfig
import com.dkb.code.factory.url_shortener.exception.BadRequestException
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component
import java.net.URI


@Component
class Validator (private val appConfig: AppConfig) {

    fun validateShortenRequest(request: JsonNode) {
        // 1. Null or empty check
        if (request.isEmpty) {
            throw BadRequestException("error.empty_request")
        }

        // 2. Required field check
        val originalUrlNode = request.get("originalUrl")?.toString()
        if (originalUrlNode == null || originalUrlNode.isBlank()) {
            throw BadRequestException("error.missing_original_url")
        }

        // 3. URL format check
//        try {
//            val url = URI.create(originalUrlNode)
//            if (url.scheme !in listOf("http", "https")) {
//                throw BadRequestException("error.invalid_protocol")
//            }
//        } catch (e: IllegalArgumentException) {
//            // Covers URISyntaxException wrapped by URI.create
//            throw BadRequestException("error.invalid_url_format")
//        }

        // 4. Maximum length check
        if (originalUrlNode.length > appConfig.urlMaxLength.toInt()) {
            throw BadRequestException("error.url_too_long")
        }

        // 5. Optional: Custom business rules
        if (originalUrlNode.contains("blocked-domain.com")) {
            throw BadRequestException("error.blocked_domain")
        }
    }
}
