package ui.pages.draw.windows

import androidx.compose.desktop.AppWindow
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import colors
import core.Message
import core.ProtocolType
import ui.core.AppContext
import ui.elements.ChannelElement
import ui.elements.WorkstationElement

fun SendMessageResultWindow(
        message: Message,
        infoPackages: Int,
        servicePackages: Int,
        time: Long
) {
    val window = AppWindow(size = IntSize(600, 300))

    window.show {
        MaterialTheme(colors = colors) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).weight(1/3f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Info packages", modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).fillMaxWidth(), textAlign = TextAlign.Center)
                    TextBox(infoPackages.toString())
                }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).weight(1/3f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Service packages", modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).fillMaxWidth(), textAlign = TextAlign.Center)
                    TextBox(servicePackages.toString())
                }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).weight(1/3f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Time (ms)", modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).fillMaxWidth(), textAlign = TextAlign.Center)
                    TextBox(time.toString())
                }
            }
        }
    }

}
//
//@Composable
//fun TextBox(text: String = "") {
//    Box(
//            modifier = Modifier.height(32.dp)
//                    .width(100.dp)
//                    .padding(start = 2.dp),
//            contentAlignment = Alignment.CenterStart
//    ) {
//        Text(text = text)
//    }
//}