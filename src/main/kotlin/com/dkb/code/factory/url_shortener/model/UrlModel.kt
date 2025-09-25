package com.dkb.code.factory.url_shortener.model

import java.time.OffsetDateTime
import java.time.ZoneOffset

data class UrlModel(
    val shortUrl: String,
    val originalUrl: String,
    val createdDate: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
    val lastModifiedDate: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)
)
