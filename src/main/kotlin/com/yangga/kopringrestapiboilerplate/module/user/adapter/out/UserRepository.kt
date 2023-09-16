package com.yangga.kopringrestapiboilerplate.module.user.adapter.out

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<UserEntity, Long> {
    override suspend fun findById(id: Long): UserEntity?
    suspend fun findByEmail(email: String): UserEntity?
    suspend fun findByEmailAndPassword(email: String, password: String): UserEntity?
}
