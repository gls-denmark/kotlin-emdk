package dk.gls.kotlin_emdk

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.gls.kemdk.DeviceSerialUtil
import dk.gls.kemdk.EMDKResult
import dk.gls.kemdk.EMDKThrowable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EMDKViewModel() : ViewModel() {

    private val _deviceResultStateFlow: MutableStateFlow<UIState> = MutableStateFlow(UIState.Init)
    val deviceSerialStateFlow = _deviceResultStateFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        UIState.Init
    )

    fun getDeviceSerial(context: Context) {
        viewModelScope.launch {
            val result =
                DeviceSerialUtil(context).retrieveAndConfigureAccessToDeviceSerial()
            when (result) {
                is EMDKResult.Failure -> {
                    when (result.error) {
                        is EMDKThrowable.ProfileLoadThrowable -> {
                            val error = result.error as EMDKThrowable.ProfileLoadThrowable
                            _deviceResultStateFlow.update { UIState.Error(error.throwable.message) }
                        }

                        is EMDKThrowable.ProfileXMLThrowable -> {
                            val error = result.error as EMDKThrowable.ProfileXMLThrowable
                            _deviceResultStateFlow.update { UIState.Error(error.emdkConfigError?.extendedConfigError) }
                        }

                        is EMDKThrowable.UnableToRetrieveSerial -> {
                            val error = result.error as EMDKThrowable.UnableToRetrieveSerial
                            _deviceResultStateFlow.update { UIState.Error(error.serialResult) }
                        }

                        else -> {
                            _deviceResultStateFlow.update { UIState.Error("Something went wrong") }
                        }
                    }
                }

                is EMDKResult.Success -> {
                    _deviceResultStateFlow.update { UIState.Success(result.value) }
                }
            }
        }
    }
}

sealed interface UIState {
    object Init : UIState
    class Success(val serialValue: String) : UIState
    class Error(val errorMessage: String?) : UIState
}
