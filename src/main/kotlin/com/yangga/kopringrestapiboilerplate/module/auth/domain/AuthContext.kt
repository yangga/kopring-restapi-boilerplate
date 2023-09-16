package com.yangga.kopringrestapiboilerplate.module.auth.domain

import com.yangga.kopringrestapiboilerplate.common.error.enums.ErrorCode
import com.yangga.kopringrestapiboilerplate.common.error.exception.ApiException
import com.yangga.kopringrestapiboilerplate.module.user.domain.User
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.ReactorContext
import org.springframework.security.core.context.SecurityContext
import reactor.core.publisher.Mono
import kotlin.coroutines.coroutineContext

data class AuthContext(
    val user: User
) {
    companion object {
        suspend fun create(): AuthContext {
            val ctx =
                coroutineContext[ReactorContext.Key]?.context?.get<Mono<SecurityContext>>(SecurityContext::class.java)
                    ?.asFlow()?.single()
            val user =
                ((ctx?.authentication?.principal as? User) ?: throw ApiException.of(ErrorCode.TOKEN_EXPIRED))

            return AuthContext(user = user)
        }
    }
}