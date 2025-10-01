package com.dkb.code.factory.url_shortener.utils

import com.dkb.code.factory.url_shortener.exception.UrlNotFoundException
import com.dkb.code.factory.url_shortener.response.SuccessResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
object SuccessResponseBuilder {

    fun build(url: String?): ResponseEntity<Any> {

        val response = SuccessResponse(
            status = 200,
            message = "Task performed successfully",
            data = url ?: throw UrlNotFoundException(),
        )
        return ResponseEntity.ok(response)
    }
}
