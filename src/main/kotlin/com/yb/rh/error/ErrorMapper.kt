package com.yb.rh.error

import org.springframework.http.HttpStatus

object ErrorMapper {
    fun toStatus(type: ErrorType): HttpStatus = when (type) {
        ErrorType.AUTHENTICATION -> HttpStatus.UNAUTHORIZED
        ErrorType.INVALID_JWT, ErrorType.CORRUPTED_DATA, ErrorType.INVALID_INPUT, ErrorType.DB_ACCESS -> HttpStatus.UNPROCESSABLE_ENTITY
        ErrorType.RESOURCE_ALREADY_EXISTS -> HttpStatus.CONFLICT
        ErrorType.RESOURCE_NOT_EXISTS, ErrorType.ENTITY_NOT_FOUND -> HttpStatus.NOT_FOUND
        ErrorType.BAD_CREDENTIAL, ErrorType.CAR_HAS_NO_OWNER, ErrorType.USER_CONSENT_REQUIRED -> HttpStatus.FORBIDDEN
        else -> HttpStatus.BAD_REQUEST
    }
}

