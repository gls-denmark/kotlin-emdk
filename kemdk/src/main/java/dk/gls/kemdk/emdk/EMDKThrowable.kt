package dk.gls.kemdk.emdk

import dk.gls.kemdk.model.EMDKConfigResult
import dk.gls.kemdk.oemInfo.OEMInfoThrowable

sealed class EMDKThrowable(override val cause: Throwable?) : Throwable() {
    /**
     * ProfileLoadThrowable is thrown when there are problems loading the emdk manager
     * The EMDK manager might be null on startup
     */
    class ProfileLoadThrowable(cause: Throwable) : EMDKThrowable(cause)

    /**
     * ProfileThrowable is related to errors when applying the configuration
     * See Readme for troubleshooting
     * @link https://techdocs.zebra.com/emdk-for-android/6-3/guide/xmlresponseguide/
     */
    class ProfileThrowable(val emdkConfigResult: EMDKConfigResult?) : EMDKThrowable(IllegalStateException("Configure the XML profile accordingly to which data you want to retrieve"))

    /**
     * UnableToRetrieveSerial happens when there is an error retrieving the device serial
     * This could be caused by a bad CallerSignature
     * See Readme for troubleshooting
     */
    @Deprecated(
        message = "API now support retrieving different OEM info instead of only serial",
        replaceWith = ReplaceWith("UnableToRetrieveData")
    )
    class UnableToRetrieveSerial(val serialResult: String?) : EMDKThrowable(IllegalStateException("Configure the CallerSignature XML accordingly to which data you want to retrieve"))

    /**
     * [UnableToRetrieveOEMInfo] happens when there is an error retrieving the requested data
     * This could be caused by a bad CallerSignature
     * Make sure your have allowed the requested OEMInfo in the CallerSignature
     * See Readme for troubleshooting
     */
    class UnableToRetrieveOEMInfo(oemInfoThrowable: OEMInfoThrowable) : EMDKThrowable(oemInfoThrowable)
}