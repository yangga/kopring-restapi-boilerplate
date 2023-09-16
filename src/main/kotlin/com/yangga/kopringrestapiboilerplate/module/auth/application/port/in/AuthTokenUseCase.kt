package com.yangga.kopringrestapiboilerplate.module.auth.application.port.`in`

import com.yangga.kopringrestapiboilerplate.module.auth.domain.BearerToken
import com.yangga.kopringrestapiboilerplate.module.auth.application.UserSecurityDetails

interface AuthTokenUseCase {

    suspend fun generate(userId: Long): BearerToken

    @Throws(Exception::class)
    fun validate(token: BearerToken)

    suspend fun load(token: BearerToken): UserSecurityDetails

}
