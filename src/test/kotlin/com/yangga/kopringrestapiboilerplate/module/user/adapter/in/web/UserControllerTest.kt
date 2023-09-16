package com.yangga.kopringrestapiboilerplate.module.user.adapter.`in`.web

import com.yangga.kopringrestapiboilerplate.common.payload.ApiResponse
import com.yangga.kopringrestapiboilerplate.support.extension.sample
import com.yangga.kopringrestapiboilerplate.module.auth.domain.BearerToken
import com.yangga.kopringrestapiboilerplate.module.user.application.UserService
import com.yangga.kopringrestapiboilerplate.module.user.domain.User
import com.yangga.kopringrestapiboilerplate.support.annotation.ControllerTest
import com.yangga.kopringrestapiboilerplate.support.spec.BehaviorSpecWithContainer
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@ControllerTest
internal class UserControllerTest(
    @MockkBean private val service: UserService,
    private val wtc: WebTestClient,
) : BehaviorSpecWithContainer({

    given("UserServiceUse.getUser has been mocked") {
        val email = "test@test.com"
        val username = "Jake"
        val token = BearerToken.sample()

        val user = User(1L, email, username)

        coEvery { service.getUser(any()) } returns user

        `when`("When get a user") {
            val response = wtc.get()
                .uri("/v1/user/${user.id}")
                .headers { it.setBearerAuth(token.value) }
                .exchange()
                .expectStatus().isOk
                .expectBody<ApiResponse<User>>()
                .returnResult().responseBody!!

            then("The user has the information what requested") {
                response.data.email shouldBe email
                response.data.username shouldBe username
            }

            then("UserServiceUse.getUser has been called") {
                coVerify { service.getUser(any()) }
            }
        }
    }
})