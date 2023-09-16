package com.yangga.kopringrestapiboilerplate.module.user.adapter.out

import com.yangga.kopringrestapiboilerplate.module.user.domain.User
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.mockk

internal class UserDaoTest : BehaviorSpec({

    val repository: UserRepository = mockk()
    val repositoryDSL: UserRepositoryDSL = mockk()
    val userDao = UserDao(repository, repositoryDSL)

    val userId = 1L
    val userEmail = "test123@test.com"
    val userPassword = "password"
    val userUsername = "test"

    given("register a user") {
        coEvery { repository.save(any()) } returns UserEntity(
            id = userId,
            email = userEmail,
            password = userPassword,
            username = userUsername
        )
        coEvery {
            repository.save(
                UserEntity(
                    email = "exist@test.com",
                    password = userPassword,
                    username = userUsername,
                )
            )
        } throws Exception("User already exists")

        `when`("When register a user") {
            val userRegistered = userDao.add(User(email = userEmail, username = userUsername), userPassword)

            then("The result is not null") {
                userRegistered shouldNotBe null
            }

            then("The result has the same email") {
                userRegistered.email shouldBe userEmail
            }
        }

        `when`("When register a user with an existing email") {
            shouldThrow<Exception> {
                userDao.add(User(email = "exist@test.com", username = userUsername), userPassword)
            }
        }
    }
})