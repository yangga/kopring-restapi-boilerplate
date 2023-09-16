package com.yangga.kopringrestapiboilerplate.module.user.application.port.out

import com.yangga.kopringrestapiboilerplate.module.user.domain.User

interface UserDaoPort {
    suspend fun add(user: User, password: String): User
    suspend fun findAll(): List<User>
    suspend fun findByEmail(email: String): User?
    suspend fun findByEmailAndPassword(email: String, password: String): User?
    suspend fun findById(id: Long): User?
}