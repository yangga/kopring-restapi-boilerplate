package com.yangga.kopringrestapiboilerplate.common.filter

import com.yangga.kopringrestapiboilerplate.common.config.LoggingWebFilterConfiguration
import org.apache.logging.log4j.kotlin.Logging
import org.reactivestreams.Publisher
import org.slf4j.MDC
import org.springframework.context.annotation.DependsOn
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream
import java.nio.channels.Channels
import java.util.*

@DependsOn("loggingFilterConfiguration")
@Component
class LoggingWebFilter(val cfg: LoggingWebFilterConfiguration) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return if (cfg.enabled && exchange.request.processNeeded(Regex(cfg.ignorePattern))) {
            chain.filter(LoggingWebExchange(exchange, cfg))
        } else {
            chain.filter(exchange)
        }
    }
}

private class LoggingWebExchange(delegate: ServerWebExchange, cfg: LoggingWebFilterConfiguration) :
    ServerWebExchangeDecorator(delegate) {
    private val requestDecorator: ServerHttpRequest
    private val responseDecorator: ServerHttpResponse

    override fun getRequest(): ServerHttpRequest {
        return requestDecorator
    }

    override fun getResponse(): ServerHttpResponse {
        return responseDecorator
    }

    init {
        val tid = delegate.request.getTid()

        requestDecorator = if (cfg.request.enabled) {
            LoggingRequestDecorator(delegate.request, tid, cfg)
        } else {
            delegate.request
        }

        responseDecorator = if (cfg.response.enabled) {
            LoggingResponseDecorator(delegate.response, tid, cfg)
        } else {
            delegate.response
        }
    }
}

private class LoggingRequestDecorator(delegate: ServerHttpRequest, tid: String, cfg: LoggingWebFilterConfiguration) :
    ServerHttpRequestDecorator(delegate),
    Logging {

    private val body: Flux<DataBuffer>?

    override fun getBody(): Flux<DataBuffer> {
        return body!!
    }

    init {
        MDC.put("tid", tid)

        logger.info(
            mapOf(
                "path" to delegate.getStrPath(),
                "query" to delegate.getStrQuery(),
                "method" to delegate.getStrMethod(),
                "headers" to delegate.getHeaderMap(),
                "ip" to delegate.getClientIp()
            )
        )

        body = if (cfg.request.body.enabled) {
            super.getBody()
                .doOnNext { buffer: DataBuffer ->
                    val bodyStream = ByteArrayOutputStream()
                    Channels.newChannel(bodyStream).write(buffer.readableByteBuffers().next())
                    val request = String(bodyStream.toByteArray())

                    MDC.put("tid", tid)

                    logger.info(
                        mapOf(
                            "request" to request
                        )
                    )
                }
        } else {
            super.getBody()
        }
    }
}

private class LoggingResponseDecorator(
    delegate: ServerHttpResponse,
    val tid: String,
    val cfg: LoggingWebFilterConfiguration
) :
    ServerHttpResponseDecorator(delegate), Logging {
    val beginAt: Long = System.currentTimeMillis()

    @Suppress("ReactiveStreamsUnusedPublisher")
    override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
        return super.writeWith(
            Flux.from(body).doOnNext { buffer: DataBuffer ->
                if (cfg.response.body.enabled) {
                    val bodyStream = ByteArrayOutputStream()
                    Channels.newChannel(bodyStream).write(buffer.readableByteBuffers().next())
                    loggingResponse(delegate.getStatus(), String(bodyStream.toByteArray()))
                } else {
                    loggingResponse(delegate.getStatus())
                }

            })
    }

    // if the body is empty, the method will be called
    override fun setComplete(): Mono<Void> {
        return loggingResponse(delegate.getStatus())
    }

    private fun loggingResponse(status: HttpStatusCode?, response: String? = null): Mono<Void> {
        MDC.put("tid", tid)
        logger.info(
            mapOf(
                "duration" to (System.currentTimeMillis() - beginAt),
                "response" to response,
                "status" to status
            )
        )
        return Mono.empty()
    }
}

private fun ServerHttpRequest.processNeeded(pattern: Regex) =
    !this.uri.path.matches(pattern)

private fun ServerHttpRequest.getTid() = this.headers["x-transaction-id"]?.firstOrNull() ?: UUID.randomUUID().toString()

private fun ServerHttpRequest.getStrPath() = this.uri.path

private fun ServerHttpRequest.getStrQuery() = this.uri.query

private fun ServerHttpRequest.getStrMethod() = Optional.ofNullable(this.method).orElse(HttpMethod.GET).name()

private fun ServerHttpRequest.getClientIp(): String {
    return this.headers["X-FORWARED-FOR"]?.firstOrNull() ?: this.remoteAddress.toString()
}

private fun ServerHttpRequest.getHeaderMap() = this.headers.keys.associateWith { this.headers[it] }

private fun ServerHttpResponse.getStatus() = this.statusCode
