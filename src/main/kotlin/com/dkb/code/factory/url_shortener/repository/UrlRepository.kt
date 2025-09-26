package com.dkb.code.factory.url_shortener.repository

import com.dkb.code.factory.url_shortener.entity.UrlEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UrlRepository : CrudRepository<UrlEntity , String> {
}