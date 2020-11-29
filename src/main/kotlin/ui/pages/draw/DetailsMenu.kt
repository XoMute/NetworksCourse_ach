package ui.pages.draw

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.core.DrawPageContext

@Composable
fun DetailsMenu(context: DrawPageContext) {
    Column(modifier = Modifier
            .width(INFO_MENU_WIDTH)
            .fillMaxHeight()
            .background(Color.White)
            .border(3.dp, Color.Black)
            .padding(10.dp)) {
        Text(text = context.infoElementState.value!!.toString())
    }
}