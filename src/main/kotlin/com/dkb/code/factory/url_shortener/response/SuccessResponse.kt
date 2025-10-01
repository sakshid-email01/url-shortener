package com.dkb.code.factory.url_shortener.response

import java.time.Instant

data class SuccessResponse(
    val status: Int,
    val message: String,
    val timestamp: Instant = Instant.now(),
    val data: String
)
