package dk.gls.kotlin_emdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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

    val test = viewModel.deviceSerialFlow.collectAsState()

    val context = LocalContext.current

    viewModel.getDeviceSerial(context)

    Text(
        text = "Get Device Serial Result:  ${test.value}!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun DisplayDeviceSerialPreview() {
    KotlinemdkTheme {
        DisplayDeviceSerial()
    }
}