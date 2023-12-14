package dk.gls.kemdk.deviceInformation

import dk.gls.kemdk.EMDKResult
import dk.gls.kemdk.emdk.EMDKThrowable
import dk.gls.kemdk.model.OEMInfo
import dk.gls.kemdk.oemInfo.OEMInfoThrowable
import kotlinx.coroutines.flow.Flow

interface IDeviceInformation {

    fun configure(): Flow<EMDKResult<Unit, EMDKThrowable>>

    fun retrieveOEMInfo(oemInfo: OEMInfo): Flow<EMDKResult<String, OEMInfoThrowable>>

}