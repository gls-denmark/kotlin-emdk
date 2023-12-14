package dk.gls.kemdk.emdk

import android.content.Context
import android.util.Log
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.ProfileManager
import dk.gls.kemdk.model.EMDKStatusCode
import dk.gls.kemdk.model.toEMDKConfigResult

class EMDKListener(
    private val context: Context,
    private val emdkListener: IEMDKListenerWrapper
) : EMDKManager.EMDKListener, ProfileManager.DataListener {

    override fun onOpened(emdkManager: EMDKManager?) {
        val manager = emdkManager?.getInstance(EMDKManager.FEATURE_TYPE.PROFILE)?.let {
            it as ProfileManager
        }

        if (manager == null) {
            Log.e(TAG, "EMDKManager was null")
            emdkListener.onError(
                EMDKThrowable.ProfileLoadThrowable(
                    RuntimeException("EMDKManager was null")
                )
            )
            return
        }

        Log.d(TAG, "EMDKManager was not null")

        manager.addDataListener(this)

        val packageName = context.packageName
        Log.d(TAG, "Loaded profile with packageName: $packageName")

        val emdkResults = manager.processProfileAsync(packageName, ProfileManager.PROFILE_FLAG.SET, null as Array<String?>?)

        if (emdkResults.statusCode == EMDKResults.STATUS_CODE.PROCESSING) {
            Log.d(TAG, "Loaded profile successfully, will receive data when callback is called. statusCode: ${emdkResults.statusCode}")
        } else {
            Log.e(TAG, "Failed to load profile, statusCode: ${emdkResults.statusCode}")
            emdkListener.onError(
                EMDKThrowable.ProfileLoadThrowable(
                    RuntimeException("Failed to initiate request to apply the profiles")
                )
            )
        }
    }

    override fun onClosed() {
        Log.d(TAG, "onClosed")
        emdkListener.onCompleted()
    }

    override fun onData(resultData: ProfileManager.ResultData?) {
        val result = resultData?.result?.toEMDKConfigResult() ?: return

        Log.d(TAG, "onData: statusCode ${result.statusCode.name}, statusString ${result.statusString}, extendedStatusMessage ${result.extendedStatusMessage}")

        if (result.statusCode == EMDKStatusCode.SUCCESS || result.statusCode == EMDKStatusCode.CHECK_XML) {
            emdkListener.onCompleted()
        } else {
            emdkListener.onError(
                EMDKThrowable.ProfileThrowable(result)
            )
        }
    }

    companion object {
        const val TAG = "EMDKListener"
    }
}