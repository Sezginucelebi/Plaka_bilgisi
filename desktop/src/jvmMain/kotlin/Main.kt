import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.material.Text
import androidx.compose.material.MaterialTheme

fun main() {
    application {
        Window(onCloseRequest = ::exitApplication, title = "YusitAPP PC Terminal") {
            MaterialTheme {
                Text("YusitAPP Masaüstü Terminaline Hoş Geldiniz!")
            }
        }
    }
}
