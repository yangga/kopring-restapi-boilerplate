package com.yangga.kopringrestapiboilerplate.module.auth.application

import com.yangga.kopringrestapiboilerplate.module.auth.domain.BearerToken
import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AuthenticationManager(
    private val tokenService: AuthTokenService
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.justOrEmpty(authentication)
            .filter { auth -> auth is BearerToken }
            .cast(BearerToken::class.java)
            .flatMap { jwt -> mono { validate(jwt) } }
    }

    private suspend fun validate(token: BearerToken): Authentication {
        tokenService.validate(token)

        val target = tokenService.load(token)

        return UsernamePasswordAuthenticationToken.authenticated(target.user, target.user, target.authorities)
    }
}