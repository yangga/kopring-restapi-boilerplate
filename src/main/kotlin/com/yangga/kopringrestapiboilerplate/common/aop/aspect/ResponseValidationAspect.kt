package com.yangga.kopringrestapiboilerplate.common.aop.aspect

import com.yangga.kopringrestapiboilerplate.common.error.enums.ErrorCode
import com.yangga.kopringrestapiboilerplate.common.error.exception.ApiException
import com.yangga.kopringrestapiboilerplate.library.extension.aop.proceedCoroutine
import com.yangga.kopringrestapiboilerplate.library.extension.aop.runCoroutine
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validator
import kotlinx.coroutines.*
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.KotlinDetector
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.stream.Collectors

@Aspect
@Component
class ResponseValidationAspect(private val validator: Validator) {
    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping) || @annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    @Throws(Throwable::class)
    fun validateResponse(pjp: ProceedingJoinPoint): Any? {
        val signature = pjp.signature as MethodSignature
        val method = signature.method

        return if (KotlinDetector.isSuspendingFunction(method)) {
            pjp.runCoroutine {
                val result = pjp.proceedCoroutine()
                validate(result!!)
                return@runCoroutine result
            }
        } else {
            val result: Any = pjp.proceed() // Controller method 실행
            if (result is Mono<*>) {
                return result.doOnNext(::validate)
            } else if (result is Flux<*>) {
                return result.doOnNext(::validate)
            }

            result
        }
    }

    @Throws(ApiException::class)
    private fun validate(obj: Any) {
        val violations: Set<ConstraintViolation<Any>> = validator.validate(obj)
        if (violations.isNotEmpty()) {
            val message = violations.stream()
                .map {
                    it.propertyPath.toString() + ": " + it.message
                }
                .collect(Collectors.joining(", "))
            throw ApiException.of(ErrorCode.BAD_RESPONSE, "Bad Response - $message")
        }
    }
}