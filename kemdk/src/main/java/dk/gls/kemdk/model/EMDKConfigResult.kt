package dk.gls.kemdk.model

import com.symbol.emdk.EMDKResults
import com.symbol.emdk.EMDKResults.STATUS_CODE
import dk.gls.kemdk.EMDKResult
import dk.gls.kemdk.emdk.EMDKThrowable
import dk.gls.kemdk.toKEMDKFailure
import dk.gls.kemdk.toKEMDKSuccess

enum class EMDKStatusCode {
    SUCCESS,
    FAILURE,
    UNKNOWN,
    NULL_POINTER,
    EMPTY_PROFILE_NAME,
    EMDK_NOT_OPENED,
    CHECK_XML,
    PREVIOUS_REQUEST_IN_PROGRESS,
    PROCESSING,
    NO_DATA_LISTENER,
    FEATURE_NOT_READY_TO_USE,
    FEATURE_NOT_SUPPORTED
}

fun STATUS_CODE.toEMDKStatusCodes(): EMDKStatusCode {
    return when (this) {
        STATUS_CODE.SUCCESS -> EMDKStatusCode.SUCCESS
        STATUS_CODE.FAILURE -> EMDKStatusCode.FAILURE
        STATUS_CODE.UNKNOWN -> EMDKStatusCode.UNKNOWN
        STATUS_CODE.NULL_POINTER -> EMDKStatusCode.NULL_POINTER
        STATUS_CODE.EMPTY_PROFILENAME -> EMDKStatusCode.EMPTY_PROFILE_NAME
        STATUS_CODE.EMDK_NOT_OPENED -> EMDKStatusCode.EMDK_NOT_OPENED
        STATUS_CODE.CHECK_XML -> EMDKStatusCode.CHECK_XML
        STATUS_CODE.PREVIOUS_REQUEST_IN_PROGRESS -> EMDKStatusCode.PREVIOUS_REQUEST_IN_PROGRESS
        STATUS_CODE.PROCESSING -> EMDKStatusCode.PROCESSING
        STATUS_CODE.NO_DATA_LISTENER -> EMDKStatusCode.NO_DATA_LISTENER
        STATUS_CODE.FEATURE_NOT_READY_TO_USE -> EMDKStatusCode.FEATURE_NOT_READY_TO_USE
        STATUS_CODE.FEATURE_NOT_SUPPORTED -> EMDKStatusCode.FEATURE_NOT_SUPPORTED
    }
}

data class EMDKConfigResult(
    val statusString: String,
    val statusCode: EMDKStatusCode,
    val extendedStatusMessage: String
)

fun EMDKResults.toEMDKConfigResult(): EMDKConfigResult {
    return EMDKConfigResult(
        statusString = statusString,
        statusCode = this.statusCode.toEMDKStatusCodes(),
        extendedStatusMessage = extendedStatusMessage
    )
}

fun EMDKConfigResult.toEMDKResult() : EMDKResult<Unit, EMDKThrowable> {
    return when(this.statusCode) {
        EMDKStatusCode.SUCCESS, EMDKStatusCode.CHECK_XML -> Unit.toKEMDKSuccess()
        else -> EMDKThrowable.ProfileLoadThrowable(IllegalStateException("The EMDK manager is not ready yet statusString: ${this.statusString}; statusCode: ${this.statusCode}; extendedStatusMessage: ${this.extendedStatusMessage}")).toKEMDKFailure()
    }
}