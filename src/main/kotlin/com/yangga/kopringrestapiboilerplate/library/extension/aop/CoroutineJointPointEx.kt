package com.yangga.kopringrestapiboilerplate.library.extension.aop

import org.aspectj.lang.ProceedingJoinPoint
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

@Suppress("UNCHECKED_CAST")
val ProceedingJoinPoint.coroutineContinuation: Continuation<Any?>
    get() = this.args.last() as Continuation<Any?>

val ProceedingJoinPoint.coroutineArgs: Array<Any?>
    get() = this.args.sliceArray(0 until this.args.size - 1)

suspend fun ProceedingJoinPoint.proceedCoroutine(
    args: Array<Any?> = this.coroutineArgs
): Any? = suspendCoroutineUninterceptedOrReturn { this.proceed(args + it) }

fun ProceedingJoinPoint.runCoroutine(
    block: suspend () -> Any?
): Any? = block.startCoroutineUninterceptedOrReturn(this.coroutineContinuation)
