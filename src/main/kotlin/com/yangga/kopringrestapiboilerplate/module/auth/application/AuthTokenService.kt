package com.yangga.kopringrestapiboilerplate.module.auth.application

import com.yangga.kopringrestapiboilerplate.common.error.enums.ErrorCode
import com.yangga.kopringrestapiboilerplate.common.error.exception.ApiException
import com.yangga.kopringrestapiboilerplate.module.auth.application.port.`in`.AuthTokenUseCase
import com.yangga.kopringrestapiboilerplate.module.user.domain.Authority
import com.yangga.kopringrestapiboilerplate.module.auth.domain.BearerToken
import com.yangga.kopringrestapiboilerplate.module.user.application.port.`in`.UserUseCase
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class AuthTokenService(
    @Value("\${spring.jwt.key}") private val key: ByteArray,
    @Value("\${spring.jwt.expiration}") private val expiration: Long,
    private val userUseCase: UserUseCase,
): AuthTokenUseCase {
    private val jwtKey = Keys.hmacShaKeyFor(key)
    private val parser = Jwts.parserBuilder().setSigningKey(jwtKey).build()

    override suspend fun generate(userId: Long): BearerToken {
        val builder = Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(dateNow())
            .setExpiration(dateAfter(expiration))
            .signWith(jwtKey)

        return BearerToken(builder.compact())
    }

    override fun validate(token: BearerToken) {
        val claims = parser.parseClaimsJws(token.value).body
        if (dateNow().after(claims.expiration)) {
            throw ApiException.of(ErrorCode.TOKEN_EXPIRED)
        }
    }

    override suspend fun load(token: BearerToken): UserSecurityDetails {
        val subject = parser.parseClaimsJws(token.value).body.subject

        val userId = subject.toLong()
        val user = userUseCase.getUser(userId)

        // to be loading authorities from database
        val authorities = listOf(Authority.USER)

        return UserSecurityDetails(user, authorities)
    }

    private fun dateNow() = Date.from(Instant.now())
    private fun dateAfter(mountToAdd: Long) = Date.from(Instant.now().plus(mountToAdd, ChronoUnit.SECONDS))
}