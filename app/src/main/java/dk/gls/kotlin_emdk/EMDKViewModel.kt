package dk.gls.kotlin_emdk

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.gls.kemdk.DeviceSerialUtil
import dk.gls.kemdk.EMDKResult
import dk.gls.kemdk.EMDKThrowable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EMDKViewModel() : ViewModel() {

    private val _deviceSerialFlow = MutableStateFlow("init")
    val deviceSerialFlow = _deviceSerialFlow.asStateFlow()

    fun getDeviceSerial(context: Context) {
        viewModelScope.launch {
            val result =
                DeviceSerialUtil(context).retrieveAndConfigureAccessToDeviceSerial()
            when (result) {
                is EMDKResult.Failure -> {
                    when (result.error) {
                        is EMDKThrowable.ProfileLoadThrowable -> {
                            val error = result.error as EMDKThrowable.ProfileLoadThrowable
                            _deviceSerialFlow.update {
                                "${error.throwable.message}"
                            }
                        }

                        is EMDKThrowable.ProfileXMLThrowable -> {
                            val error = result.error as EMDKThrowable.ProfileXMLThrowable
                            _deviceSerialFlow.update {
                                "${error.emdkResult?.extendedStatusMessage}"
                            }
                        }

                        is EMDKThrowable.UnableToRetrieveSerial -> {
                            val error = result.error as EMDKThrowable.UnableToRetrieveSerial
                            _deviceSerialFlow.update {
                                "${error.serialResult}"
                            }
                        }

                        else -> {
                            _deviceSerialFlow.update {
                                result.error.message ?: "Something went wrong"
                            }
                        }
                    }
                }

                is EMDKResult.Success -> {
                    _deviceSerialFlow.update { result.value }
                }
            }
        }
    }


}