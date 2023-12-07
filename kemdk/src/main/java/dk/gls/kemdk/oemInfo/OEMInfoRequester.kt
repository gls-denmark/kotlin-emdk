package dk.gls.kemdk.oemInfo

import android.content.Context
import android.util.Log
import dk.gls.kemdk.EMDKResult
import dk.gls.kemdk.model.OEMInfo
import dk.gls.kemdk.model.getApiUri
import dk.gls.kemdk.toKEMDKFailure
import dk.gls.kemdk.toKEMDKSuccess

/**
 * Wrapper for the OEMInfo content resolver interface
 */
class OEMInfoRequester(
    private val context: Context
) : IOEMInfoRequester {
    /**
     * Retrieve OEMInfo from phone based on parsed @URI
     * For clarity, this code calls ContentResolver.query() on the UI thread but production code should perform queries asynchronously.
     * @link https://developer.android.com/guide/topics/providers/content-provider-basics.html for more information
     **/
    override fun retrieveOEMInfo(oemInfo: OEMInfo): EMDKResult<String, OEMInfoThrowable> {
        val uri = oemInfo.getApiUri()
        val cursor = context.contentResolver.query(uri, null, null, null, null)

        try {
            if (cursor == null || cursor.count < 1) {
                val errorMsg = "The app does not have access to call OEM service. Please assign access to $uri through MX"
                Log.e(TAG, errorMsg)
                return OEMInfoThrowable.CursorNotAvailable(errorMsg).toKEMDKFailure()
            }

            while (cursor.moveToNext()) {
                if (cursor.columnCount == 0) {
                    val errorMsg = "No data in the cursor. Can happen on non-WAN devices. $uri does not exist on this device"
                    Log.d(TAG, errorMsg)
                    return OEMInfoThrowable.CursorEmpty(errorMsg).toKEMDKFailure()
                } else {
                    for (i in 0 until cursor.columnCount) {
                        val columnIndex = cursor.getColumnIndex(cursor.getColumnName(i))
                        val data = cursor.getString(columnIndex)

                        Log.d(TAG, "Column $i=${cursor.getColumnName(i)}; Data $i=$data")
                        return if(data.isNotBlank()) {
                            data.toKEMDKSuccess()
                        } else {
                            OEMInfoThrowable.DataNotAccessible.toKEMDKFailure()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception reading data for column")
            return OEMInfoThrowable.ExceptionReadingData(e).toKEMDKFailure()
        } finally {
            cursor?.close()
        }

        return OEMInfoThrowable.DataNotAccessible.toKEMDKFailure()
    }

    companion object {
        const val TAG = "OEM_INFO_REQUESTER"
    }

}