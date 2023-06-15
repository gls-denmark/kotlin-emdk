package dk.gls.kemdk

import com.symbol.emdk.EMDKResults

sealed class EMDKThrowable : Throwable() {
    class ProfileLoadThrowable(val throwable: Throwable) : EMDKThrowable()
    class ProfileXMLThrowable(val emdkResult: EMDKResults?) : EMDKThrowable()
    class UnableToRetrieveSerial(val serialResult: String?) : EMDKThrowable()
}