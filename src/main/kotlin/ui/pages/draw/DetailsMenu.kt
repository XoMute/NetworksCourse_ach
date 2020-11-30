package ui.pages.draw

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.core.DrawPageContext

@Composable
fun NodeDetailsMenu(context: DrawPageContext) {
    Column(modifier = Modifier
            .width(INFO_MENU_WIDTH)
            .fillMaxHeight()
            .background(Color.White)
            .border(3.dp, Color.Black)
            .padding(10.dp)) {
        Text(text = context.infoElementState.value!!.toString())
        Spacer(Modifier
                .fillMaxWidth()
                .height(50.dp))
        Button(onClick = {
            context.showRoutingTable()
        }) {
            Text(text = "Show Routing Table")
        }
    }
}

@Composable
fun ChannelDetailsMenu(context: DrawPageContext) {
    Column(modifier = Modifier
            .width(INFO_MENU_WIDTH)
            .fillMaxHeight()
            .background(Color.White)
            .border(3.dp, Color.Black)
            .padding(10.dp)) {
        Text(text  = "Channel", modifier = Modifier.align(Alignment.CenterHorizontally))
        Text(text = context.infoElementState.value!!.toString())
        Spacer(Modifier
                .fillMaxWidth()
                .height(50.dp))
        Button(onClick = {
            context.showRoutingTable()
        }) {
        }
    }
}