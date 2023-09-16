package com.yangga.kopringrestapiboilerplate.module.user.adapter.out

import com.yangga.kopringrestapiboilerplate.support.annotation.DatabaseTest
import com.yangga.kopringrestapiboilerplate.support.spec.BehaviorSpecWithContainer
import io.kotest.matchers.comparables.shouldBeGreaterThan

@DatabaseTest
internal class UserRepositoryDSLTest(
    private val userRepositoryDSL: UserRepositoryDSL
) : BehaviorSpecWithContainer({

    given("users table has some data") {
        userRepositoryDSL.add(UserEntity(
            email = "testDSL@test.com",
            username = "testDSL",
            password = "password"))

        `when`("retrieve all users") {
            val users = userRepositoryDSL.findAll()

            then("users is not empty") {
                users.size shouldBeGreaterThan 0
            }
        }
    }
})
