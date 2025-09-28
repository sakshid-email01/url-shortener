package com.dkb.code.factory.url_shortener.exception


    /**
     * Thrown when a short key cannot be resolved to an original URL.
     */
    class UrlNotFoundException(message: String? = "No URL mapping found") : RuntimeException(message)
