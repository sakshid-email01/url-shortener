package com.dkb.code.factory.url_shortener.dto


import jakarta.validation.constraints.NotBlank

data class ShortenRequest(
    @field:NotBlank(message = "originalUrl is required")
    val originalUrl: String
)
