package ui.pages.routing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ui.menu.Tab

@Composable
fun RoutingPage(navigator: (Tab, Any?) -> Unit) = Row {
    Column(modifier = Modifier
            .weight(1f)) {
        Text(text = "LOL")
    }
}