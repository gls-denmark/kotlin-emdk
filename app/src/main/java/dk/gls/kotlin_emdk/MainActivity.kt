package dk.gls.kotlin_emdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
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
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.gls.kotlin_emdk.ui.theme.KotlinemdkTheme


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

    val serialResult = viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(Unit) {

        viewModel.configure(context)
    }

    LaunchedEffect(Unit) {
        viewModel.getDeviceSerial()
    }

    Column {
        Text(
            text = getText("Configuration", serialResult.value.configure),
            modifier = modifier
        )

        Text(
            text = getText("OEMInfo", serialResult.value.oemInfoInfoUIState),
            modifier = modifier
        )
    }
}

private fun getText(type: String, value: InfoUIState): String {
    return when (value) {
        is InfoUIState.Error -> {
            "$type \n Error occurred: ${value.errorMessage}"
        }

        is InfoUIState.Success -> {
            "$type \n Result: ${value.serialValue}"
        }

        is InfoUIState.Init -> {
            "$type \n Loading"
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