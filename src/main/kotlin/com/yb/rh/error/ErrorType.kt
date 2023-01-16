package com.yb.rh.error

enum class ErrorType {
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
    ILLEGAL_APPLICATION_STATE,
    FEATURE_DISABLED,
    UPDATE_EMAIL_TRACKING_STATUS_FAILED,
    SQS_PRODUCE_FAILED,
    BAD_CREDENTIAL
}