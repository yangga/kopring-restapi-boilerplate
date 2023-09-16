package com.yangga.kopringrestapiboilerplate.module.auth.adapter.`in`.web

import com.yangga.kopringrestapiboilerplate.common.payload.ApiResponse
import com.yangga.kopringrestapiboilerplate.module.auth.application.port.`in`.AuthTokenUseCase
import com.yangga.kopringrestapiboilerplate.module.auth.domain.AuthContext
import com.yangga.kopringrestapiboilerplate.module.auth.domain.LoginUser
import com.yangga.kopringrestapiboilerplate.module.auth.domain.RegisterUser
import com.yangga.kopringrestapiboilerplate.module.auth.domain.UserWithToken
import com.yangga.kopringrestapiboilerplate.module.user.application.port.`in`.UserUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "Auth", description = "Auth API")
@Validated
@RestController()
@RequestMapping("/v1/auth")
class AuthController(
    private val authTokenUserCase: AuthTokenUseCase,
    private val userUseCase: UserUseCase
) : Logging {

    @Operation(description = "register user")
    @PostMapping("/register")
    suspend fun registerUser(@Valid @RequestBody body: RegisterUser): ApiResponse<UserWithToken> {
        val user = userUseCase.registerUser(com.yangga.kopringrestapiboilerplate.module.user.domain.RegisterUser(
            email = body.email,
            password = body.password,
            username = body.username,
        ))

        val token = authTokenUserCase.generate(user.id).value
        return ApiResponse.success(UserWithToken(user, token))
    }

    @Operation(description = "login user")
    @PostMapping("/login")
    suspend fun loginUser(@Valid @RequestBody body: LoginUser): ApiResponse<UserWithToken> {
        val user = userUseCase.loginUser(body.email, body.password)
        val token = authTokenUserCase.generate(user.id).value
        return ApiResponse.success(UserWithToken(user, token))
    }

    @Operation(description = "test token")
    @GetMapping("/testToken")
    suspend fun testToken(): ApiResponse<String> {
        val context = AuthContext.create()
        return ApiResponse.success(context.toString())
    }

}