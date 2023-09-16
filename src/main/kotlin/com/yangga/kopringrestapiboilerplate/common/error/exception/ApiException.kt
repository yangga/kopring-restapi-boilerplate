package com.yangga.kopringrestapiboilerplate.common.error.exception

import com.yangga.kopringrestapiboilerplate.common.error.enums.ErrorCode

open class ApiException(val code: ErrorCode, message: String?, val data: Any?) : Throwable(message) {

    companion object {
        fun of(code: ErrorCode, message: String? = null, data: Any? = null): ApiException {
            return ApiException(code, message, data)
        }
    }

    override fun toString(): String {
        return "ApiException(code=$code, message=$message, data=$data)"
    }
}

