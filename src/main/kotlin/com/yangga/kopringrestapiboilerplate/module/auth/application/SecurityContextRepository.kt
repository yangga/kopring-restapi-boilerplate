package com.yangga.kopringrestapiboilerplate.module.auth.application

import com.yangga.kopringrestapiboilerplate.module.auth.domain.BearerToken
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Repository
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Repository
class SecurityContextRepository(private val authenticationManager: ReactiveAuthenticationManager) :
    ServerSecurityContextRepository {
    override fun save(swe: ServerWebExchange, sc: SecurityContext) = Mono.empty<Void>()

    override fun load(swe: ServerWebExchange): Mono<SecurityContext> {
        return Mono.justOrEmpty<String>(swe.request.headers.getFirst(HttpHeaders.AUTHORIZATION))
            .filter { authHeader: String -> authHeader.startsWith("Bearer ") }
            .flatMap { authHeader: String ->
                val authToken = authHeader.substring(7)
                val auth = BearerToken(authToken)
                authenticationManager.authenticate(auth).map { SecurityContextImpl(it) }
            }
    }
}
