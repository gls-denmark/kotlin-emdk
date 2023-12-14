package dk.gls.kemdk.emdk

import android.content.Context
import com.symbol.emdk.EMDKManager
import dk.gls.kemdk.EMDKResult
import dk.gls.kemdk.model.toEMDKConfigResult
import dk.gls.kemdk.model.toEMDKResult
import dk.gls.kemdk.toKEMDKFailure
import dk.gls.kemdk.toKEMDKSuccess
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class EMDKIntegration(private val context: Context) : IEMDKIntegration {

    override suspend fun configure(): EMDKResult<Unit, EMDKThrowable> = suspendCancellableCoroutine { continuation ->
        val emdkListenerWrapper = object : IEMDKListenerWrapper {
            override fun onCompleted() {
                continuation.resume(Unit.toKEMDKSuccess())
            }

            override fun onError(throwable: EMDKThrowable) {
                continuation.resume(throwable.toKEMDKFailure())
            }
        }

        val emdkListener = EMDKListener(
            context = context,
            emdkListener = emdkListenerWrapper
        )

        val res = EMDKManager.getEMDKManager(context, emdkListener)
            .toEMDKConfigResult()
            .toEMDKResult()

        if (res is EMDKResult.Failure) {
            continuation.resume(res)
        }

    }
}