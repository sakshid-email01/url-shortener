package com.dkb.code.factory.url_shortener.utils

import com.dkb.code.factory.url_shortener.exception.BadRequestException
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component
import java.net.MalformedURLException
import java.net.URI


@Component
class Validator {

    companion object {
        private const val MAX_URL_LENGTH = 2048
        private val ALLOWED_PROTOCOLS = listOf("http", "https")
    }

    fun validateShortenRequest(request: JsonNode) {
        // 1. Null or empty check
        if (request.isEmpty) {
            throw BadRequestException("Request body cannot be empty")
        }

        // 2. Required field check
        val originalUrlNode = request.get("originalUrl")?.asText()
        if (originalUrlNode == null || originalUrlNode.isBlank()) {
            throw BadRequestException("Field 'originalUrl' is required")
        }

        // 3. URL format check
        try {
            val url = URI.create(originalUrlNode)
            if (url.scheme !in ALLOWED_PROTOCOLS) {
                throw BadRequestException("URL must start with http or https")
            }
        } catch (e: MalformedURLException) {
            throw BadRequestException("Invalid URL format")
        }

        // 4. Maximum length check
        if (originalUrlNode.length > 2048) {
            throw BadRequestException("URL too long (maximum 2048 characters allowed)")
        }

        // 5. Optional: Custom business rules
        if (originalUrlNode.contains("blocked-domain.com")) {
            throw BadRequestException("URLs from this domain are not allowed")
        }
    }
}
