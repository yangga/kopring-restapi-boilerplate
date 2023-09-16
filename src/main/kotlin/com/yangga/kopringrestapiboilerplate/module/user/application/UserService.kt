package com.yangga.kopringrestapiboilerplate.module.user.application

import com.yangga.kopringrestapiboilerplate.common.error.enums.ErrorCode
import com.yangga.kopringrestapiboilerplate.common.error.exception.ApiException
import com.yangga.kopringrestapiboilerplate.module.user.application.port.`in`.UserUseCase
import com.yangga.kopringrestapiboilerplate.module.user.application.port.out.UserDaoPort
import com.yangga.kopringrestapiboilerplate.module.user.domain.RegisterUser
import com.yangga.kopringrestapiboilerplate.module.user.domain.User
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userDaoPort: UserDaoPort
) : UserUseCase {
    override suspend fun registerUser(data: RegisterUser): User {
        val oldUser = userDaoPort.findByEmail(data.email)
        if (oldUser != null) {
            throw ApiException.of(ErrorCode.ALREADY_EXIST, "User already exist", data.email)
        }

        val user = User(
            email = data.email,
            username = data.username,
        )

        return userDaoPort.add(user, data.password)
    }

    override suspend fun loginUser(email: String, password: String): User {
        return userDaoPort.findByEmailAndPassword(email, password) ?: throw ApiException.of(
            ErrorCode.NOT_FOUND,
            "User not found",
            email
        )
    }

    override suspend fun getUser(id: Long): User {
        return userDaoPort.findById(id) ?: throw ApiException.of(ErrorCode.NOT_FOUND, "User not found", id)
    }

    override suspend fun getUserAll(): List<User> {
        return userDaoPort.findAll()
    }
}