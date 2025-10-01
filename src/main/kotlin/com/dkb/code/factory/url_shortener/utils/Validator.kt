package com.dkb.code.factory.url_shortener.utils

import com.dkb.code.factory.url_shortener.config.GlobalConfig
import com.dkb.code.factory.url_shortener.dto.ShortenRequest
import com.dkb.code.factory.url_shortener.exception.BadRequestException
import org.springframework.stereotype.Component



@Component
class Validator (private val globalConfig: GlobalConfig) {

    fun validateShortenRequest(request: ShortenRequest) {
        val originalUrl = request.originalUrl

        // 1. Null or empty check
        if (originalUrl.isEmpty() || originalUrl.isBlank()) {
            throw BadRequestException("error.missing_original_url")
        }

        // 2. Maximum length check
        if (originalUrl.length > globalConfig.urlMaxLength.toInt()) {
            throw BadRequestException("error.url_too_long")
        }

        // 3. Optional: Custom business rules
        if (originalUrl.contains("blocked-domain.com")) {
            throw BadRequestException("error.blocked_domain")
        }
    }
}
