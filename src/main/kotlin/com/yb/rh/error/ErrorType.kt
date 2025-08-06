package com.yb.rh.error

import com.fasterxml.jackson.annotation.JsonValue

enum class ErrorType {
    USER_CONSENT_REQUIRED,
    AUTHENTICATION,
    INVALID_JWT,
    CORRUPTED_DATA,
    DB_ACCESS,
    HTTP_CALL,
    RESOURCE_ALREADY_EXISTS,
    RESOURCE_NOT_EXISTS,
    INVALID_INPUT,
    UNKNOWN,
    ENTITY_NOT_FOUND,
    BAD_CREDENTIAL,
    CAR_HAS_NO_OWNER;

    @JsonValue
    fun getValue(): String = name
}