package dk.gls.kemdk

import com.symbol.emdk.EMDKResults

sealed class EMDKThrowable : Throwable() {
    /**
     * ProfileLoadThrowable is thrown when there are problems loading the emdk manager
     * The EMDK manager might be null on startup
     */
    class ProfileLoadThrowable(val throwable: Throwable) : EMDKThrowable()

    /**
     * ProfileXMLThrowable is related to errors in the EDMKConfig.xml
     * See Readme for troubleshooting
     */
    class ProfileXMLThrowable(val emdkResult: EMDKResults?) : EMDKThrowable()

    /**
     * UnableToRetrieveSerial happens when there is an error retrieving the device serial
     * This could be caused by a bad CallerSignature
     * See Readme for troubleshooting
     */
    class UnableToRetrieveSerial(val serialResult: String?) : EMDKThrowable()
}