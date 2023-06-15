package dk.gls.kemdk

import android.content.Context
import android.net.Uri
import android.util.Log
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.ProfileManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DeviceSerialUtil(val context: Context) {

    /** Uses the EMDKManager to configure the device and enable retrieval of Device Serial. **/
    suspend fun retrieveAndConfigureAccessToDeviceSerial(): EMDKResult<String, EMDKThrowable> =
        suspendCancellableCoroutine { continuation ->
            //If the EMDKManager is already configured for the current build type, we can retrieve the device serial immediately
            val deviceSerial = retrieveDeviceSerial()
            Log.d("kemdk", "retrieveDeviceSerial: $deviceSerial")
            if (deviceSerial != null && !deviceSerial.contains("Error")) {
                continuation.resume(deviceSerial.toKEMDKSuccess())
            } else {
                val emdkManagerResults: EMDKResults = EMDKManager.getEMDKManager(
                    context,
                    object : EMDKManager.EMDKListener, ProfileManager.DataListener {
                        override fun onOpened(emdkManager: EMDKManager?) {
                            val manager =
                                emdkManager?.getInstance(EMDKManager.FEATURE_TYPE.PROFILE)?.let {
                                    it as ProfileManager
                                }
                            //TODO We should consider implementing a retry logic in case the returned EMDK manager was null, because that could happen on startup
                            if (manager == null) {
                                Log.e("kemdk", "EMDKManager was null")
                                continuation.resume(
                                    EMDKThrowable.ProfileLoadThrowable(
                                        RuntimeException("EMDKManager was null")
                                    ).toKEMDKFailure()
                                )
                                return
                            }

                            manager.addDataListener(this)

                            val packageName = context.packageName
                            Log.d("kemdk", "Loaded profile with packageName: $packageName")
                            val emdkResults = manager.processProfileAsync(
                                packageName,
                                ProfileManager.PROFILE_FLAG.SET,
                                null as Array<String?>?
                            )

                            Log.d("kemdk", "Loaded profile statusCode: ${emdkResults.statusCode}")
                            if (emdkResults.statusCode == EMDKResults.STATUS_CODE.PROCESSING) {
                                //Applying the profile, status will be returned through the registered callback
                            } else {
                                //Failed to initiate request to apply the profiles.
                                if (continuation.isActive) {
                                    continuation.resume(
                                        EMDKThrowable.ProfileLoadThrowable(
                                            RuntimeException("Failed to initiate request to apply the profiles")
                                        ).toKEMDKFailure()
                                    )
                                }
                            }
                        }

                        override fun onClosed() {
                            Log.d("kemdk", "onClosed")
                        }

                        override fun onData(resultData: ProfileManager.ResultData?) {
                            val result = resultData?.result
                            if (result?.statusCode == EMDKResults.STATUS_CODE.CHECK_XML) {
                                Log.d(
                                    "kemdk",
                                    "statusCode ${EMDKResults.STATUS_CODE.CHECK_XML}, responseXml ${result.statusString}"
                                )
                                //TODO The documentation says 'Parse the XML in result.statusString to know the status of profiles applied'. Do we want to parse the xml?
                                //I think we have configured EMDK correctly by now, let's try and retrieve the serial
                                val deviceSerial = retrieveDeviceSerial()
                                Log.d("kemdk", "onData: retrieveDeviceSerial: $deviceSerial")
                                if (continuation.isActive) {
                                    if (deviceSerial != null && !deviceSerial.contains("Error")) {
                                        continuation.resume(deviceSerial.toKEMDKSuccess())
                                    } else {
                                        continuation.resume(
                                            EMDKThrowable.UnableToRetrieveSerial(
                                                deviceSerial
                                            ).toKEMDKFailure()
                                        )
                                    }
                                }
                            } else if (result?.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
                                //Error occurred in applying the profile.
                                Log.d(
                                    "kemdk",
                                    "statusCode error: ${result?.statusCode} ${result?.extendedStatusMessage}"
                                )
                                if (continuation.isActive) {
                                    continuation.resume(
                                        EMDKThrowable.ProfileXMLThrowable(result).toKEMDKFailure()
                                    )
                                }
                            }
                        }
                    })

                Log.d("kemdk", "getEMDKManager: ${emdkManagerResults.statusCode}")
            }
        }

    /** Retrieve device serial. Be aware that the device should either be configured using Stage Now or above [retrieveAndConfigureAccessToDeviceSerial] function at least once before it is possible to retrieve the serial number **/
    fun retrieveDeviceSerial() = retrieveOEMInfo(Uri.parse(URI_SERIAL), context)

    /** Retrieve OEMInfo from phone based on parsed @URI **/
    private fun retrieveOEMInfo(uri: Uri, context: Context): String? {
        //  For clarity, this code calls ContentResolver.query() on the UI thread but production code should perform queries asynchronously.
        //  See https://developer.android.com/guide/topics/providers/content-provider-basics.html for more information
        val cursor = context.contentResolver.query(uri, null, null, null, null)

        if (cursor == null || cursor.count < 1) {
            val errorMsg = "Error: This app does not have access to call OEM service. " +
                    "Please assign access to " + uri + " through MX.  See ReadMe for more information"
            Log.d("kemdk", errorMsg)
            return errorMsg
        }
        while (cursor.moveToNext()) {
            if (cursor.columnCount == 0) {
                //  No data in the cursor.  I have seen this happen on non-WAN devices
                var errorMsg = "Error: $uri does not exist on this device"
                Log.d("kemdk", errorMsg)
                cursor.close()
                return errorMsg
            } else {
                for (i in 0 until cursor.columnCount) {
                    Log.v("kemdk", "column $i=${cursor.getColumnName(i)}")
                    try {
                        val columnIndex = cursor.getColumnIndex(cursor.getColumnName(i))
                        val data = cursor.getString(columnIndex)
                        Log.d("kemdk", "Column Data $i=$data")
                        cursor.close()
                        return data
                    } catch (e: Exception) {
                        cursor.close()
                        Log.d(
                            "kemdk",
                            "Exception reading data for column ${cursor.getColumnName(i)}"
                        )
                    }
                }
            }
        }
        cursor.close()
        return null
    }

    companion object {
        private const val URI_SERIAL = "content://oem_info/oem.zebra.secure/build_serial"
    }

}