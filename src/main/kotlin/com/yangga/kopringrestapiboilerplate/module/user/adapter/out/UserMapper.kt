package com.yangga.kopringrestapiboilerplate.module.user.adapter.out

import com.yangga.kopringrestapiboilerplate.module.user.domain.User

class UserMapper {
    companion object {
        fun toDomain(userEntity: UserEntity): User {
            return User(
                id = userEntity.id,
                email = userEntity.email,
                username = userEntity.username,
            )
        }
    }
}