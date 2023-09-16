package com.yangga.kopringrestapiboilerplate.module.auth.domain

import com.yangga.kopringrestapiboilerplate.module.user.domain.User
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import org.hibernate.validator.constraints.Length

data class UserWithToken(
    @Schema(description = "user")
    @field: Valid
    val user: User,

    @Schema(description = "token")
    @field: Length(min = 1)
    val token: String,
)