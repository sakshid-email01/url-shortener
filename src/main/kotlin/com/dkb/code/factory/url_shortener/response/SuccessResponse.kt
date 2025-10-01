package com.dkb.code.factory.url_shortener.response

data class SuccessResponse(
    val status: Int,
    val message: String,
    val data: String
)
