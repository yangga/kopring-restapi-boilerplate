package com.yangga.kopringrestapiboilerplate.module.user.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import org.hibernate.validator.constraints.Length

data class RegisterUser(
    @Schema(description = "email", example = "test@test.com")
    @field: Email
    val email: String,

    @Schema(description = "name", example = "password123!@#")
    @field: Length(min = 4, max = 40)
    val password: String,

    @Schema(description = "name", example = "Jake")
    @field: Length(min = 4, max = 40)
    val username: String,
)