package ui.appInterface

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.unit.dp
import ui.core.AppContext
import ui.elements.base.ElementType

val INFO_MENU_WIDTH = 300.dp
val CONTROL_PANEL_HEIGHT = 100.dp

@Composable
fun DrawPage(context: AppContext) = Row {
    Column(modifier = Modifier
            .weight(1f)) {
        Row(modifier = Modifier.fillMaxSize().weight(1f)) {
            WithConstraints {
                DrawCanvas(context, modifier = Modifier.width(if (context.showInfoState.value) maxWidth - INFO_MENU_WIDTH else maxWidth))
            }
            if (context.showInfoState.value) {
                when (context.infoElementState.value!!.type) {
                    ElementType.CHANNEL -> ChannelDetailsMenu(context)
                    else -> ConnectableElementMenu(context)
                }
            }
        }
        ControlPanel(context)
    }
}
