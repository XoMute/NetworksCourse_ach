package ui.pages.draw

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import core.ProtocolType
import ui.core.AppContext
import ui.elements.base.Element
import ui.pages.draw.windows.TextBox

data class NodeInfo(
        val id: Int,
        val x: Int,
        val y: Int,
        val enabled: Boolean
)

@Composable
fun RouterDetailsMenu(context: AppContext) {
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
            Text(text = "Apply changes")
        }
        Spacer(Modifier.height(50.dp))
        Button(onClick = {
            context.showRoutingTable()
        }) {
            Text(text = "Show Routing Table")
        }
        Spacer(Modifier.height(50.dp))
        Button(onClick = {
            context.showRoutingTable()
        }) {
            Text(text = "Delete")
        }
    }
}

@Composable
fun WorkstationDetailsMenu(context: AppContext) {
    val elem = context.infoElementState.value!!
    val nodeInfoState = remember { mutableStateOf(NodeInfo(elem.id, elem.pos.x.toInt(), elem.pos.y.toInt(), elem.enabled)) }
    if (nodeInfoState.value.id != elem.id) {
        nodeInfoState.value = NodeInfo(elem.id, elem.pos.x.toInt(), elem.pos.y.toInt(), elem.enabled)
    }
    val changedState = remember { mutableStateOf(false) }
    Column(modifier = Modifier
            .width(INFO_MENU_WIDTH)
            .fillMaxHeight()
            .background(Color.White)
            .border(3.dp, Color.Black)
            .padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(text = context.infoElementState.value!!.toString())
        Text(text = "Element position:")
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth().height(80.dp)) {
            TextField(value = nodeInfoState.value.x.toString(),
                    label = {
                        Text(text = "X:")
                    },
                    singleLine = true,
                    onValueChange = { value ->
                        changedState.value = true
                        nodeInfoState.value = nodeInfoState.value.copy(
                                x = value.toIntOrNull()
                                        ?: nodeInfoState.value.x)
                    },
                    modifier = Modifier.weight(1 / 2f))
            TextField(value = nodeInfoState.value.y.toString(),
                    label = {
                        Text(text = "Y:")
                    },
                    singleLine = true,
                    onValueChange = { value ->
                        changedState.value = true
                        nodeInfoState.value = nodeInfoState.value.copy(
                                y = value.toIntOrNull()
                                        ?: nodeInfoState.value.y)
                    },
                    modifier = Modifier.weight(1 / 2f))
        }
        Column {
            Row {
                TextBox("Enabled")
                Checkbox(checked = nodeInfoState.value.enabled, onCheckedChange = {
                    nodeInfoState.value = nodeInfoState.value.copy(enabled = true)
                    changedState.value = true
                })
            }
            Row {
                TextBox("Disabled")
                Checkbox(checked = !nodeInfoState.value.enabled, onCheckedChange = {
                    nodeInfoState.value = nodeInfoState.value.copy(enabled = false)
                    changedState.value = true
                })
            }
        }
        Spacer(Modifier
                .fillMaxWidth()
                .height(10.dp))
        Button(onClick = {
            context.infoElementState.value = elem.apply {
                pos = Offset(nodeInfoState.value.x.toFloat(), nodeInfoState.value.y.toFloat())
            }
            if (nodeInfoState.value.enabled) {
                context.enableNode(elem.id)
            } else {
                context.disableNode(elem.id)
            }
            changedState.value = false
        }, enabled = changedState.value) {
            Text(text = "Apply changes")
        }
        Spacer(Modifier.height(50.dp))
        Button(onClick = {
            context.showRoutingTable()
        }) {
            Text(text = "Show Routing Table")
        }
        Spacer(Modifier.height(50.dp))
        Button(onClick = {
            context.deleteNode(context.infoElementState.value!!.id)
        }) {
            Text(text = "Delete")
        }
    }
}

@Composable
fun ChannelDetailsMenu(context: AppContext) {
    Column(modifier = Modifier
            .width(INFO_MENU_WIDTH)
            .fillMaxHeight()
            .background(Color.White)
            .border(3.dp, Color.Black)
            .padding(10.dp)) {
        Text(text = "Channel", modifier = Modifier.align(Alignment.CenterHorizontally))
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