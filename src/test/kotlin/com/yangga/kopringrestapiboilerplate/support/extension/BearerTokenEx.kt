package com.yangga.kopringrestapiboilerplate.support.extension

import com.yangga.kopringrestapiboilerplate.module.auth.domain.BearerToken
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

fun BearerToken.Companion.sample() = BearerToken(
    Jwts.builder()
        .setSubject("1")
        .setIssuedAt(Date.from(Instant.now()))
        .setExpiration(Date.from(Instant.now().plus(3600, ChronoUnit.SECONDS)))
        .signWith(
            Keys.hmacShaKeyFor(
                "this-is-a-sample-key-for-jwt*this-is-a-sample-key-for-jwt*this-is-a-sample-key-for-jwt*this-is-a-sample-key-for-jwt*this-is-a-sample-key-for-jwt*this-is-a-sample-key-for-jwt*this-is-a-sample-key-for-jwt*this-is-a-sample-key-for-jwt*this-is-a-sample-key-for-jwt*this-is-a-sample-key-for-jwt*".toByteArray(
                    Charsets.UTF_8
                )
            )
        )
        .compact()
)