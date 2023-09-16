package com.yangga.kopringrestapiboilerplate.common.error.aop.advice

import com.yangga.kopringrestapiboilerplate.common.error.enums.ErrorCode
import com.yangga.kopringrestapiboilerplate.common.payload.ApiResponse
import io.r2dbc.spi.R2dbcException
import io.sentry.Sentry
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.core.publisher.Mono

@RestControllerAdvice
class DatabaseExceptionHandler : Logging {

    @ExceptionHandler(DuplicateKeyException::class)
    fun handlerDuplicateKeyException(ex: DuplicateKeyException): Mono<ResponseEntity<ApiResponse.Error>> {
        logger.catching(Level.DEBUG, ex)

        return Mono.just(
            ResponseEntity(
                ApiResponse.error(ErrorCode.ALREADY_EXIST, "already exist"),
                ErrorCode.ALREADY_EXIST.httpStatusCode
            )
        )
    }

    @ExceptionHandler(R2dbcException::class)
    fun handleR2dbcException(ex: R2dbcException): Mono<ResponseEntity<ApiResponse.Error>> {
        logger.catching(Level.ERROR, ex)
        Sentry.captureException(ex)

        return Mono.just(
            ResponseEntity(
                ApiResponse.error(ErrorCode.DATABASE_ERROR, "database error"),
                ErrorCode.DATABASE_ERROR.httpStatusCode
            )
        )
    }
}