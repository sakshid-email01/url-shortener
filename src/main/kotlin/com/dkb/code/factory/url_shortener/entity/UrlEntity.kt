package com.dkb.code.factory.url_shortener.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime


@Entity
@Table(
    name = "urls",
    indexes = [
        Index(name = "idx_original_url", columnList = "original_url") // secondary index for faster lookups
    ]
)
data class UrlEntity(

    @Id
    @Column(name = "short_url", unique = true, nullable = false, length = 255)
    val shortUrl: String,

    @Column(nullable = false, unique = true)
    val originalUrl: String,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false,
        columnDefinition = "TIMESTAMP WITH TIME ZONE")
    var createdAt: OffsetDateTime? = null,

)
