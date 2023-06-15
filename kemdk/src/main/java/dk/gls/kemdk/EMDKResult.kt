package dk.gls.kemdk

sealed class EMDKResult<out Value, out Failure> {
    class Success<out Value>(val value: Value) : EMDKResult<Value, Nothing>()
    class Failure<out Failure>(val error: Failure) : EMDKResult<Nothing, Failure>()

    companion object
}


fun <T> T.toKEMDKSuccess(): EMDKResult.Success<T> = EMDKResult.Success(this)

fun <Error> Error.toKEMDKFailure() = EMDKResult.Failure(this)
