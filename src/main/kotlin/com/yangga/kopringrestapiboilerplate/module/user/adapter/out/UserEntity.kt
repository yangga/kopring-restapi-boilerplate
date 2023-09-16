package com.yangga.kopringrestapiboilerplate.module.user.adapter.out

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class UserEntity(
    @Id val id: Long = 0,
    @Column("email") val email: String,
    @Column("password") val password: String,
    @Column("username") val username: String,
)