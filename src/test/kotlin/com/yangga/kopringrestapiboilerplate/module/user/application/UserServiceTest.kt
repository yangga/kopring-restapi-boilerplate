package com.yangga.kopringrestapiboilerplate.module.user.application

import com.yangga.kopringrestapiboilerplate.common.error.enums.ErrorCode
import com.yangga.kopringrestapiboilerplate.common.error.exception.ApiException
import com.yangga.kopringrestapiboilerplate.module.user.adapter.out.UserDao
import com.yangga.kopringrestapiboilerplate.module.user.domain.User
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

internal class UserServiceTest: BehaviorSpec({
    given("mocking method getUser") {
        val dao: UserDao = mockk()
        val service = UserService(dao)

        val user = User(1L, "test@test.com", "Jake")
        coEvery { dao.findById(any()) } returns null
        coEvery { dao.findById(user.id) } returns user

        `when`("when get a exist user") {
            val v = service.getUser(user.id)

            then("id should be expected") {
                v.id shouldBe user.id
            }
            then("email is not null") {
                v.email shouldNotBe null
            }
            then("username is not null") {
                v.username shouldNotBe null
            }
            then("service.getUser() has been called") {
                coVerify { service.getUser(user.id) }
            }
        }

        `when`("when get a not exist user") {
            val exception = shouldThrow<ApiException> {
                service.getUser(9999L)
            }

            then("it took NOT_FOUND exception") {
                exception.code shouldBe ErrorCode.NOT_FOUND
            }
            then("service.getUser() has been called") {
                coVerify { service.getUser(9999L) }
            }
        }
    }
})