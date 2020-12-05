package ui.appInterface.windows

import androidx.compose.desktop.AppWindow
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import colors
import routing.Message

fun SendMessageResultWindow(
        message: Message,
        infoPackages: Int,
        servicePackages: Int,
        time: Long
) {
    val window = AppWindow(size = IntSize(600, 300))

    window.show {
        MaterialTheme(colors = colors) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).weight(1 / 3f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Message size(bytes)", modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).fillMaxWidth(), textAlign = TextAlign.Center)
                        TextBox(message.size.toString())
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).weight(1 / 3f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Package size(bytes)", modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).fillMaxWidth(), textAlign = TextAlign.Center)
                        TextBox(message.packageSize.toString())
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).weight(1 / 3f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Protocol", modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).fillMaxWidth(), textAlign = TextAlign.Center)
                        TextBox(message.protocol.toString())
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).weight(1 / 3f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Info packages", modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).fillMaxWidth(), textAlign = TextAlign.Center)
                        TextBox(infoPackages.toString())
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).weight(1 / 3f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Service packages", modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).fillMaxWidth(), textAlign = TextAlign.Center)
                        TextBox(servicePackages.toString())
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).weight(1 / 3f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Time (ms)", modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).fillMaxWidth(), textAlign = TextAlign.Center)
                        TextBox(time.toString())
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).weight(1 / 2f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Total info traffic(bytes)", modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).fillMaxWidth(), textAlign = TextAlign.Center)
                        TextBox((infoPackages * message.packageSize).toString())
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).weight(1 / 2f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Total service traffic(bytes)", modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).fillMaxWidth(), textAlign = TextAlign.Center)
                        TextBox((servicePackages * message.servicePackageSize).toString())
                    }
                }
            }
        }
    }
}

fun SendMessageErrorWindow(
        message: String
) {
    val window = AppWindow(size = IntSize(600, 300))

    window.show {
        MaterialTheme(colors = colors) {
            Text(message)
        }
    }

}