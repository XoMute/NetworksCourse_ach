package ui.pages.draw

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.core.DrawPageContext
import ui.menu.Tab

val INFO_MENU_WIDTH = 300.dp
val CONTROL_PANEL_HEIGHT = 100.dp

@Composable
fun DrawPage(context: DrawPageContext, navigator: (Tab, Any?) -> Unit) = Row {
    Column(modifier = Modifier
            .weight(1f)) {
        Row(modifier = Modifier.fillMaxSize().weight(1f)) {
            DrawCanvas(context)
            if (context.showInfoState.value) {
                DetailsMenu(context)
            }
        }
        ControlPanel(context)
    }
}
