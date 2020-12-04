import androidx.compose.desktop.Window
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import ui.MainUI

val colors = lightColors(
        primary = Color.Blue,
        secondary = Color.Cyan
)

fun main() {
    Window(title = "Course_ach", size = IntSize(1600, 900)) {
        MaterialTheme(colors = colors, typography = Typography()) {
            MainUI()
        }
    }
}
