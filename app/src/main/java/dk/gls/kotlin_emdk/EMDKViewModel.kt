package dk.gls.kotlin_emdk

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.gls.kemdk.EMDKResult
import dk.gls.kemdk.KEMDK
import dk.gls.kemdk.model.OEMInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EMDKViewModel : ViewModel() {

    private val _configureFlow: MutableStateFlow<InfoUIState> = MutableStateFlow(InfoUIState.Init)

    fun configure(context: Context) {
        KEMDK.setup(context = context)

        viewModelScope.launch {
            KEMDK.configure().map {
                val value = when (it) {
                    is EMDKResult.Failure -> InfoUIState.Error(it.error.cause?.message)
                    is EMDKResult.Success -> InfoUIState.Success("Success!")
                }
                _configureFlow.emit(value)
            }.collect()
        }
    }

    private val _deviceResultStateFlow: MutableStateFlow<InfoUIState> = MutableStateFlow(InfoUIState.Init)

    fun getDeviceSerial() {
        viewModelScope.launch {
            KEMDK.retrieveOEMInfo(OEMInfo.BT_MAC)
                .map { result ->
                    when (result) {
                        is EMDKResult.Failure -> {
                            _deviceResultStateFlow.update { InfoUIState.Error(result.error.toString()) }
                        }

                        is EMDKResult.Success -> {
                            _deviceResultStateFlow.update { InfoUIState.Success(result.value) }
                        }
                    }
                }
                .collect()
        }
    }

    val uiState = combine(_configureFlow, _deviceResultStateFlow) { configure, deviceResult ->
        UIState(
            configure = configure,
            oemInfoInfoUIState = deviceResult
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        UIState()
    )
}

data class UIState(
    val configure: InfoUIState = InfoUIState.Init,
    val oemInfoInfoUIState: InfoUIState = InfoUIState.Init
)

sealed interface InfoUIState {
    object Init : InfoUIState
    class Success(val serialValue: String) : InfoUIState
    class Error(val errorMessage: String?) : InfoUIState
}
