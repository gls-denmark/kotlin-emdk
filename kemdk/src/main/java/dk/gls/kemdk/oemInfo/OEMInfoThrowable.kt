package dk.gls.kemdk.oemInfo

sealed class OEMInfoThrowable(message: String, cause: Throwable? = null) : Throwable(message = message, cause) {

    class CursorNotAvailable(message: String) : OEMInfoThrowable(message)

    class CursorEmpty(message: String) : OEMInfoThrowable(message, IllegalStateException("Empty cursor"))

    class ExceptionReadingData(cause: Throwable) : OEMInfoThrowable("Exception reading data", cause)

    object DataNotAccessible : OEMInfoThrowable("Data not present")

}