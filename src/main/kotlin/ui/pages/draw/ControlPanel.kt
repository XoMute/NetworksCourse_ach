package ui.pages.draw

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import ui.core.DrawPageContext
import ui.elements.ControlPanelElement
import ui.elements.base.ElementType

@Composable
fun ControlPanel(context: DrawPageContext) {
    Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
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
                context.selectedTypeState.value = ElementType.CONNECTION
            }
        }
    }
}