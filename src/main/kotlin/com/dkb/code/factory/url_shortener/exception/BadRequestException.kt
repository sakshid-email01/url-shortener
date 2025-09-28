package com.dkb.code.factory.url_shortener.exception


import org.springframework.http.HttpStatus

class BadRequestException(
    override val message: String,
    val status: HttpStatus = HttpStatus.BAD_REQUEST
) : RuntimeException(message)
