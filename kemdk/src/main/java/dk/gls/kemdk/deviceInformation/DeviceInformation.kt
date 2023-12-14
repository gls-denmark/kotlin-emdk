package dk.gls.kemdk.deviceInformation

import dk.gls.kemdk.EMDKResult
import dk.gls.kemdk.emdk.EMDKIntegration
import dk.gls.kemdk.emdk.EMDKThrowable
import dk.gls.kemdk.model.OEMInfo
import dk.gls.kemdk.model.RetryConfiguration
import dk.gls.kemdk.oemInfo.IOEMInfoRequester
import dk.gls.kemdk.oemInfo.OEMInfoThrowable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

class DeviceInformation(
    private val emdkIntegration: EMDKIntegration,
    private val oemInfoRequester: IOEMInfoRequester,
    private val retryConfiguration: RetryConfiguration
) : IDeviceInformation {

    override fun configure(): Flow<EMDKResult<Unit, EMDKThrowable>> {
        return flow {
            configureWrapper(attempt = 0)
        }
    }

    private suspend fun FlowCollector<EMDKResult<Unit, EMDKThrowable>>.configureWrapper(attempt: Int) {
        val res = emdkIntegration.configure()
        emit(res)

        if(res is EMDKResult.Failure) {
            if (attempt <= retryConfiguration.maxAttempts) {
                delay(retryConfiguration.timeoutMs)
                configureWrapper(attempt + 1)
            }
        }
    }

    override fun retrieveOEMInfo(oemInfo: OEMInfo): Flow<EMDKResult<String, OEMInfoThrowable>> {
        return flow {
            requestWrapper(oemInfo, attempt = 0)
        }
    }

    private suspend fun FlowCollector<EMDKResult<String, OEMInfoThrowable>>.requestWrapper(oemInfo: OEMInfo, attempt: Int) {
        val res = oemInfoRequester.retrieveOEMInfo(oemInfo)
        emit(res)

        if (res is EMDKResult.Failure) {
            if (attempt <= retryConfiguration.maxAttempts) {
                delay(retryConfiguration.timeoutMs)
                requestWrapper(oemInfo, attempt + 1)
            }
        }
    }

}