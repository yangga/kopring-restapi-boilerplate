package com.yangga.kopringrestapiboilerplate.module.user.application.port.`in`

import com.yangga.kopringrestapiboilerplate.module.user.domain.RegisterUser
import com.yangga.kopringrestapiboilerplate.module.user.domain.User

interface UserUseCase {
    suspend fun registerUser(data: RegisterUser): User
    suspend fun loginUser(email: String, password: String): User
    suspend fun getUser(id: Long): User
    suspend fun getUserAll(): List<User>
}