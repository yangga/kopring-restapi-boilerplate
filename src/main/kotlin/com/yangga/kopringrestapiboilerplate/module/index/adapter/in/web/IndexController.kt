package com.yangga.kopringrestapiboilerplate.module.index.adapter.`in`.web

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.net.URI

@Hidden
@Tag(name = "Index", description = "Default API")
@RestController()
@RequestMapping("/")
class IndexController {
    @GetMapping("/")
    fun index(response: ServerHttpResponse): Mono<Void> {
        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT)
        response.headers.location = URI.create("/api-docs")
        return response.setComplete()
    }

    @GetMapping("/health")
    fun hello() = Mono.just("OK")
}