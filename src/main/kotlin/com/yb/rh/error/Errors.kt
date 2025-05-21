package com.yb.rh.error

import com.fasterxml.jackson.annotation.JsonAlias
import com.yb.rh.error.ErrorType.*
import org.eclipse.jetty.http.HttpStatus

open class RHException(
    errorMessage: String,
    open val errorType: ErrorType = UNKNOWN,
    throwable: Throwable? = null,
) : RuntimeException(errorMessage, throwable)

sealed class RhError(
    message: String,
    errorType: ErrorType = UNKNOWN,
    throwable: Throwable? = null
) : RHException(message, errorType, throwable)

class InternalServerError(msg: String) :
    HttpException(msg, errorType = UNKNOWN, httpStatus = HttpStatus.INTERNAL_SERVER_ERROR_500)

open class HttpException(
    errorMessage: String,
    throwable: Throwable? = null,
    errorType: ErrorType = HTTP_CALL,
    open val httpStatus: Int,
) : RHException(errorMessage, errorType, throwable)

data class WithErrorMessage(
    val clientName: ClientName,
    val requestType: String,
    override val httpStatus: Int,
    val msg: String,
    override val errorType: ErrorType,
) : HttpException("Call `$requestType` has failed due to `$msg`", httpStatus = httpStatus, errorType = errorType) {
    fun toLogMessage(): String {
        return "Call to service `$clientName` with the request to `$requestType` has failed due to `$msg`. Http Status Code: $httpStatus"
    }

    companion object {
        fun fromErrorMsg(errorMessage: String) =
            when (ExternalServiceErrors.fromErrDisplayNameToHttpStatus(errorMessage)) {
                401 -> AUTHENTICATION
                409 -> RESOURCE_ALREADY_EXISTS
                else -> UNKNOWN
            }
    }
}

class NoResponseException(requestType: String, httpStatus: Int = HttpStatus.REQUEST_TIMEOUT_408) :
    HttpException("Call for `$requestType` didn't get a response", httpStatus = httpStatus)

class NoResponseBody(call: String, httpStatus: Int = HttpStatus.OK_200) :
    HttpException("`$call` has no body. Http Status Code: $httpStatus", httpStatus = httpStatus)

class GetDbRecordFailed(val table: String) : RhError("Failed to get a record from table `$table`", DB_ACCESS)
class SaveDbRecordFailed(val table: String) : RhError("Failed to save a record to table `$table`", DB_ACCESS)
class ExternalApiError(override val message: String) : RhError(message, HTTP_CALL)
class InvalidCarData(override val message: String) : RhError(message, INVALID_INPUT)
class InvalidUserData(override val message: String) : RhError(message, INVALID_INPUT)
class NotificationError(override val message: String) : RhError(message, HTTP_CALL)

class EnrichmentError(propertyName: String, step: String) :
    RHException("Failed to enrich With $propertyName in $step")

class CreateEmailTrackingError(notificationId: String?, prescreenId: Int) :
    RHException("Failed to create email tracking for $notificationId in $prescreenId")

class PlanIdNotSuitToAppError(applicationId: Int, planId: Int) :
    RHException(
        "SelectedPlanId : $planId doesn't suit given applicationId : $applicationId",
        errorType = INVALID_INPUT
    )

class ResendLeadEmailException(applicationId: Int) :
    RHException("No Approved PS1 application $applicationId doesn't have any mail to resend", INVALID_INPUT)

class PropertyNotExistsOnToken(propertyName: String, throwable: Throwable? = null) :
    RHException("$propertyName does not exists on JWT", INVALID_JWT, throwable)

class PropertyNotOfType(propertyValue: String, expectedClass: String) :
    RHException("`$propertyValue` is not of type $expectedClass", errorType = INVALID_JWT)

class TokenNotFound(errorMessage: String) :
    RHException(errorMessage, errorType = AUTHENTICATION)

class ApplicationNotFound(applicationId: Int) :
    RHException("No application exists for applicationId : $applicationId", errorType = ENTITY_NOT_FOUND)

class DtoCreation(dtoClass: String, throwable: Throwable) :
    RHException("Failed creating DTO of type `$dtoClass`", CORRUPTED_DATA, throwable)

class BadCredentials(message: String) :
    RHException(message, errorType = BAD_CREDENTIAL)

class AllianceAuthenticationFailed(allianceId: Int) :
    RHException("Alliance with id `$allianceId` is not Authenticated`", errorType = AUTHENTICATION)

class AllianceNotFound(allianceId: Int) :
    RHException("allianceId `$allianceId` does not exist`", errorType = RESOURCE_NOT_EXISTS)

class UnauthorizedPrescreen() :
    RHException("Prescreen feature not enable for this platform`", errorType = FEATURE_DISABLED)

class EntityNotFound(entityClass: Class<*>, entityId: String?) :
    RHException("${entityClass.name} Entity with Id : $entityId Not Found", errorType = ENTITY_NOT_FOUND)

data class HttpErrorBody(@JsonAlias(value = ["code", "errorCode"]) val code: String, val message: String?) {
    override fun toString(): String {
        return message ?: ""
    }
}

enum class ExternalServiceErrors(val httpStatus: Int, val displayName: String) {
    ALLIANCE_WITH_SUNBIT_KEY_NOT_EXISTS(401, "alliance with given sunbitKey wasn't found or is disabled"),
    ALLIANCE_SECRET_NOT_MATCHES_KEY(401, "alliance sunbit secret does not match the sunbitKey"),
    ALLIANCE_KEY_AND_ID_NOT_MATCH(401, "alliance with sunbitKey does not match to the given allianceId"),
    SAME_LOCATION_ALREADY_EXISTS(409, "Same location was already added for this alliance"),
    SAME_ADDRESS_ALREADY_EXISTS(411, "Same address was already added for this alliance");

    companion object {
        val map = values().associateBy(ExternalServiceErrors::displayName)
        fun fromErrDisplayNameToHttpStatus(displayName: String) = map[displayName]?.httpStatus ?: 500
    }
}

enum class ClientName() {
    RHSERVICE
}