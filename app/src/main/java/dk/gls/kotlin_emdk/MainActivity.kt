package dk.gls.kotlin_emdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.gls.kotlin_emdk.ui.theme.KotlinemdkTheme
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinemdkTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DisplayDeviceSerial()
                }
            }
        }
    }
}

@Composable
fun DisplayDeviceSerial(modifier: Modifier = Modifier) {
    val viewModel: EMDKViewModel = viewModel()

    val serialResult = viewModel.deviceSerialStateFlow.collectAsStateWithLifecycle()

    val context = LocalContext.current

    if (serialResult.value is UIState.Init) {
        LaunchedEffect(Unit) {
            viewModel.getDeviceSerial(context)
        }
    }

    Text(
        text = getText(serialResult.value),
        modifier = modifier
    )
}

private fun getText(value: UIState): String{
    return when(value){
        is UIState.Error -> {
            "An Error occurred when getting device serial:\n${(value as UIState.Error).errorMessage}!"
        }
        is UIState.Success -> {
            "Device Serial:\n${(value as UIState.Success).serialValue}!"
        }
        is UIState.Init -> {
            "Getting Device Serial"
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DisplayDeviceSerialPreview() {
    KotlinemdkTheme {
        DisplayDeviceSerial()
    }
}