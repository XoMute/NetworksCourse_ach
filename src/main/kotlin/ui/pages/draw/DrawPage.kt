package ui.pages.draw

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ui.core.DrawPageContext

@Composable
fun DrawPage() = Row {
    Column(modifier = Modifier
            .weight(1f)) {
        val context = DrawPageContext()
        DrawCanvas(Modifier.weight(1f), context)
        ControlPanel(context)
    }
}
