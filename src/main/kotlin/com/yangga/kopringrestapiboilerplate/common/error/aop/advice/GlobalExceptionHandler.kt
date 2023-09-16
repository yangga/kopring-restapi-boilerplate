package com.yangga.kopringrestapiboilerplate.common.error.aop.advice

import com.yangga.kopringrestapiboilerplate.common.error.enums.ErrorCode
import com.yangga.kopringrestapiboilerplate.common.error.exception.ApiException
import com.yangga.kopringrestapiboilerplate.common.payload.ApiResponse
import io.sentry.Sentry
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.kotlin.Logging
import org.apache.logging.log4j.spi.StandardLevel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.core.publisher.Mono

@RestControllerAdvice
class GlobalExceptionHandler : Logging {

    @ExceptionHandler(ApiException::class)
    fun handleApiException(ex: ApiException): Mono<ResponseEntity<ApiResponse.Error>> {
        logger.catching(ex.code.level, ex)

        if (ex.code.level.isCritical()) {
            Sentry.captureException(ex)
        }

        return Mono.just(
            ResponseEntity(
                ApiResponse.error(ex.code, ex.message ?: "unknown error", ex.data),
                ex.code.httpStatusCode
            )
        )
    }

    @ExceptionHandler(Throwable::class)
    fun handleGenericException(ex: Throwable): Mono<ResponseEntity<ApiResponse.Error>> {
        logger.catching(Level.ERROR, ex)
        Sentry.captureException(ex)

        return Mono.just(
            ResponseEntity(
                ApiResponse.error(ErrorCode.UNKNOWN, ex.message ?: "unknown error"),
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        )
    }
}

private fun Level.isCritical(): Boolean {
    return this.intLevel() != 0 && this.intLevel() <= StandardLevel.ERROR.intLevel()
}

