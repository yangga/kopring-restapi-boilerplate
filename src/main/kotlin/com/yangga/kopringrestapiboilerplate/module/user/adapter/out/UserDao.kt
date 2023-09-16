package com.yangga.kopringrestapiboilerplate.module.user.adapter.out

import com.yangga.kopringrestapiboilerplate.module.user.application.port.out.UserDaoPort
import com.yangga.kopringrestapiboilerplate.module.user.domain.User
import org.springframework.stereotype.Component

@Component
class UserDao(
    private val userRepository: UserRepository,
    private val userRepositoryDSL: UserRepositoryDSL,
) : UserDaoPort {
    override suspend fun add(user: User, password: String): User {
        val entity = UserEntity(
            email = user.email,
            password = password,
            username = user.username,
        )

        return UserMapper.toDomain(userRepository.save(entity))
    }

    override suspend fun findAll(): List<User> {
        return userRepositoryDSL.findAll().map(UserMapper::toDomain)
    }

    override suspend fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)?.let { UserMapper.toDomain(it) }
    }

    override suspend fun findByEmailAndPassword(email: String, password: String): User? {
        return userRepository.findByEmailAndPassword(email, password)?.let { UserMapper.toDomain(it) }
    }

    override suspend fun findById(id: Long): User? {
        return userRepository.findById(id)?.let { UserMapper.toDomain(it) }
    }
}