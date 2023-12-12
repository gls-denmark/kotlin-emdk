package dk.gls.kemdk

import android.content.Context
import dk.gls.kemdk.emdk.EMDKThrowable
import dk.gls.kemdk.model.OEMInfo
import dk.gls.kemdk.model.RetryConfiguration
import dk.gls.kemdk.oemInfo.OEMInfoThrowable
import kotlinx.coroutines.flow.Flow

/**
 * [IKotlinEMDK]
 * A interface for retrieving the device OEMInfo
 * Uses the EMDK android library developed by Zebra
 * More information can be found here:
 * https://www.zebra.com/us/en/software/mobile-computer-software/emdk-for-android.html
 */
interface IKotlinEMDK {

    /** Uses the EMDKManager to configure the device and enable retrieval of Device OEMInfo **/
    fun setup(context: Context, retryConfiguration: RetryConfiguration = RetryConfiguration())

    /**
     * [configure]
     * Subscribe to configuration state changes
     * The EMDK is not necessarily ready on launch, and therefore it can be beneficial to monitor this state
     */
    fun configure() : Flow<EMDKResult<Unit, EMDKThrowable>>

    /**
     * [retrieveOEMInfo]
     * Get OEMInfo as a flow
     * Retrieve specific [OEMInfo]
     * Retries the specified amounts in the setup function [RetryConfiguration] parameter
     * Be aware that the device should either be configured using Stage Now or above [configure] function at least once before it is possible to retrieve the any [OEMInfo]
    **/
    fun retrieveOEMInfo(oemInfo: OEMInfo): Flow<EMDKResult<String, EMDKThrowable.UnableToRetrieveOEMInfo>>
}