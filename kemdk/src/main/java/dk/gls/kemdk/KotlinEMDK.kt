package dk.gls.kemdk

import android.content.Context
import dk.gls.kemdk.deviceInformation.DeviceInformation
import dk.gls.kemdk.deviceInformation.IDeviceInformation
import dk.gls.kemdk.emdk.EMDKIntegration
import dk.gls.kemdk.emdk.EMDKThrowable
import dk.gls.kemdk.model.OEMInfo
import dk.gls.kemdk.model.RetryConfiguration
import dk.gls.kemdk.oemInfo.OEMInfoRequester
import dk.gls.kemdk.oemInfo.OEMInfoThrowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

open class KotlinEMDK : IKotlinEMDK {

    private lateinit var _deviceInformation: IDeviceInformation

    override fun setup(context: Context, retryConfiguration: RetryConfiguration) {
        val emdkIntegration = EMDKIntegration(context)
        val oemInfoRequester = OEMInfoRequester(context)

        _deviceInformation = DeviceInformation(
            emdkIntegration = emdkIntegration,
            oemInfoRequester = oemInfoRequester,
            retryConfiguration = retryConfiguration
        )
    }

    override fun configure(): Flow<EMDKResult<Unit, EMDKThrowable>> {
        return _deviceInformation.configure()
    }

    override fun retrieveOEMInfo(oemInfo: OEMInfo): Flow<EMDKResult<String, EMDKThrowable.UnableToRetrieveOEMInfo>> {
        return _deviceInformation.retrieveOEMInfo(oemInfo).map { result ->
            return@map when(result) {
                is EMDKResult.Failure -> EMDKThrowable.UnableToRetrieveOEMInfo(result.error).toKEMDKFailure()
                is EMDKResult.Success -> result
            }
        }
    }
}

object KEMDK : KotlinEMDK()