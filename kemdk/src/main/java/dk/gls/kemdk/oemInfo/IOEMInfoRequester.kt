package dk.gls.kemdk.oemInfo

import dk.gls.kemdk.EMDKResult
import dk.gls.kemdk.model.OEMInfo

interface IOEMInfoRequester {

    fun retrieveOEMInfo(oemInfo: OEMInfo): EMDKResult<String, OEMInfoThrowable>
}