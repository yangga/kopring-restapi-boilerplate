package com.yangga.kopringrestapiboilerplate.module.user.adapter.`in`.web

import com.yangga.kopringrestapiboilerplate.common.payload.ApiResponse
import com.yangga.kopringrestapiboilerplate.module.user.application.port.`in`.UserUseCase
import com.yangga.kopringrestapiboilerplate.module.user.domain.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Min

import org.apache.logging.log4j.kotlin.Logging
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "User", description = "User API")
@Validated
@RestController()
@RequestMapping("/v1/user")
class UserController(private val userUseCase: UserUseCase) : Logging {

    @Operation(description = "get all users")
    @GetMapping("/all")
    suspend fun getUserAll(): ApiResponse.List<User> {
        val list = userUseCase.getUserAll()
        return ApiResponse.list(list, list.size)
    }

    @Operation(description = "get an user")
    @GetMapping("/{id}")
    suspend fun getUser(@Min(1) @PathVariable id: Long): ApiResponse<User> {
        return ApiResponse.success(userUseCase.getUser(id))
    }

}