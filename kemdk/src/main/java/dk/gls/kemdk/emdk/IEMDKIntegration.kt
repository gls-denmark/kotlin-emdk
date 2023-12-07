package dk.gls.kemdk.emdk

import dk.gls.kemdk.EMDKResult
import dk.gls.kemdk.model.EMDKConfigResult
import kotlinx.coroutines.flow.Flow

interface IEMDKIntegration {

    suspend fun configure(): EMDKResult<Unit, EMDKThrowable>

}