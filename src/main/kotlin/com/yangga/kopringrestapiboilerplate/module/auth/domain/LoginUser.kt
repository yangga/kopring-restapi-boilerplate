package com.yangga.kopringrestapiboilerplate.module.auth.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import org.hibernate.validator.constraints.Length

data class LoginUser(
    @Schema(description = "email", example = "test@test.com")
    @field: Email
    val email: String,

    @Schema(description = "name", example = "password")
    @field: Length(min = 4, max = 40)
    val password: String,
)