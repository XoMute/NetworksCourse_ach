package ui.pages.routing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import core.RoutingResult
import ui.core.DrawPageContext
import ui.menu.Tab
import ui.pages.draw.ControlPanel
import ui.pages.draw.DetailsMenu
import ui.pages.draw.DrawCanvas

@Composable
fun RoutingPage(result: RoutingResult, navigator: (Tab, Any?) -> Unit) = Row {
    Column(modifier = Modifier
            .weight(1f)) {
        Text(text = "LOL")
    }
}