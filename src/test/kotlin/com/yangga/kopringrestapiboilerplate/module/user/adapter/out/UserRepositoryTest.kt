package com.yangga.kopringrestapiboilerplate.module.user.adapter.out

import com.yangga.kopringrestapiboilerplate.support.annotation.DatabaseTest
import com.yangga.kopringrestapiboilerplate.support.spec.BehaviorSpecWithContainer
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

@DatabaseTest
internal class UserRepositoryTest(
    private val userRepository: UserRepository
) : BehaviorSpecWithContainer({

    given("user table is empty") {
        `when`("register a user") {
            val entity = userRepository.save(
                UserEntity(
                    email = "test@test.com",
                    password = "test",
                    username = "username",
                )
            )

            then("user entity which is saved has an id") {
                entity.id shouldBeGreaterThan 0
            }

            then("user entity which is saved has the same email") {
                entity.email shouldBe "test@test.com"
            }
        }

        `when`("find a user which registered from UserRepositoryDSLTest") {
            val userEntity = userRepository.findByEmail("testDSL@test.com")

            then("userEntity should be null") {
                userEntity shouldBe null
            }
        }
    }
})
