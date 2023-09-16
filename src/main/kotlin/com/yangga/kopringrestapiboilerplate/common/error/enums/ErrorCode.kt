package com.yangga.kopringrestapiboilerplate.common.error.enums

import org.apache.logging.log4j.Level
import org.springframework.http.HttpStatus

enum class ErrorCode(val level: Level, val httpStatusCode: HttpStatus) {
    UNKNOWN(Level.ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST(Level.DEBUG, HttpStatus.BAD_REQUEST),
    BAD_RESPONSE(Level.ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
    TOKEN_EXPIRED(Level.DEBUG, HttpStatus.UNAUTHORIZED),
    NOT_FOUND(Level.DEBUG, HttpStatus.NOT_FOUND),
    ALREADY_EXIST(Level.DEBUG, HttpStatus.CONFLICT),
    DATABASE_ERROR(Level.ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
}