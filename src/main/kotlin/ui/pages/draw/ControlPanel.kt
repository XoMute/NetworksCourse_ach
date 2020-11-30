package ui.pages.draw

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import ui.core.DrawPageContext
import ui.elements.compose.ControlPanelElement
import ui.elements.base.ElementType
import ui.menu.Tab

@Composable
fun ControlPanel(context: DrawPageContext, navigator: (Tab, Any?) -> Unit) {
    Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .height(CONTROL_PANEL_HEIGHT)
                    .border(BorderStroke(2.dp, SolidColor(Color.Black)))
    ) {
        Row(modifier = Modifier
                .padding(start = 50.dp, top = 5.dp, bottom = 5.dp)
                .align(Alignment.Start)) {
            ControlPanelElement("workstation.png") {
                context.selectedTypeState.value = ElementType.WORKSTATION
            }
            Spacer(modifier = Modifier
                    .width(32.dp))
            ControlPanelElement("router.png") {
                context.selectedTypeState.value = ElementType.COMMUNICATION_NODE
            }
            Spacer(modifier = Modifier
                    .width(32.dp))
            ControlPanelElement("line.png") {
                context.selectedTypeState.value = ElementType.CHANNEL
            }
            Spacer(modifier = Modifier
                    .width(100.dp))
            Button(onClick = { context.sendMessage(navigator) }) {
                Text(text = "Send Message")
            }
        }
    }
}