package com.yangga.kopringrestapiboilerplate.module.auth.application

import com.yangga.kopringrestapiboilerplate.module.user.domain.Authority
import com.yangga.kopringrestapiboilerplate.module.user.domain.User

data class UserSecurityDetails(
    val user: User,
    val authorities: Collection<Authority>,
)