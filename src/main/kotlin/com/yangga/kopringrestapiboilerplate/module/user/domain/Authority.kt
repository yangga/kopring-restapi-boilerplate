package com.yangga.kopringrestapiboilerplate.module.user.domain

import org.springframework.security.core.GrantedAuthority

enum class Authority: GrantedAuthority {
    ADMIN,
    USER,
    ANONYMOUS;

    companion object {
        fun of(authority: String): Authority {
            return when (authority) {
                "ADMIN" -> ADMIN
                "USER" -> USER
                else -> ANONYMOUS
            }
        }
    }

    override fun getAuthority() = this.name
}