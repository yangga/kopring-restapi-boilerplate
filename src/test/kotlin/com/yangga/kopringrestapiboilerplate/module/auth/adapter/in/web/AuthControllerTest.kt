package com.yangga.kopringrestapiboilerplate.module.auth.adapter.`in`.web

import com.yangga.kopringrestapiboilerplate.common.payload.ApiResponse
import com.yangga.kopringrestapiboilerplate.support.extension.sample
import com.yangga.kopringrestapiboilerplate.module.auth.application.AuthTokenService
import com.yangga.kopringrestapiboilerplate.module.auth.domain.BearerToken
import com.yangga.kopringrestapiboilerplate.module.auth.domain.UserWithToken
import com.yangga.kopringrestapiboilerplate.support.annotation.ControllerTest
import com.yangga.kopringrestapiboilerplate.support.spec.BehaviorSpecWithContainer
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.BodyInserters

@ControllerTest
internal class AuthControllerTest(
    @MockkBean private val service: AuthTokenService,
    private val wtc: WebTestClient,
) : BehaviorSpecWithContainer({

    given("AuthTokenService.generate has been mocked") {
        val email =  "test@test.com"
        val password = "abcdefghijlmn"
        val username = "Jake"
        val token = BearerToken.sample()

        coEvery { service.generate(any()) } returns token

        `when`("When register a user") {
            val response = wtc.post()
                .uri("/v1/auth/register")
                .body(
                    BodyInserters.fromValue(
                        mapOf(
                            "email" to email,
                            "password" to password,
                            "username" to username,
                        )
                    )
                )
                .exchange()
                .expectStatus().isOk
                .expectBody<ApiResponse<UserWithToken>>()
                .returnResult().responseBody!!

            then("The registered user has the information what requested") {
                response.data.user.email shouldBe email
                response.data.user.username shouldBe username
            }

            then("The token has been sent") {
                response.data.token shouldBe token.value
            }

            then("AuthTokenService.generate has been called") {
                coVerify { service.generate(any()) }
            }
        }
    }
})