package desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.material.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

fun main() {
    application {
        Window(onCloseRequest = ::exitApplication, title = "YusitAPP PC Terminal") {
            MaterialTheme {
                GreetingView()
            }
        }
    }
}

@Composable
fun GreetingView() {
    Text("YusitAPP Masaüstü Terminaline Hoş Geldiniz!")
}
