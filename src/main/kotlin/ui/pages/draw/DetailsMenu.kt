package ui.pages.draw

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.unit.dp
import ui.core.AppContext
import ui.core.CHANNEL_WEIGHTS
import ui.elements.ChannelElement
import ui.elements.ChannelType
import ui.pages.draw.windows.TextBox

data class NodeInfo(
        val id: Int,
        val x: Int,
        val y: Int,
        val enabled: Boolean
)

@Composable
fun ConnectableElementMenu(context: AppContext) {
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
            Text("Enabled")
            Switch(checked = nodeInfoState.value.enabled, onCheckedChange = {
                nodeInfoState.value = nodeInfoState.value.copy(enabled = it)
                changedState.value = true
            })
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

data class ChannelInfo(
        val id: Int,
        val weight: Int,
        val type: ChannelType,
        val satellite: Boolean = false,
        val enabled: Boolean = true
)

@Composable
fun ChannelDetailsMenu(context: AppContext) {
    val elem = context.infoElementState.value!! as ChannelElement
    val channelInfoState = remember { mutableStateOf(ChannelInfo(elem.id, elem.weight, elem.channelType)) }
    if (channelInfoState.value.id != elem.id) {
        channelInfoState.value = ChannelInfo(elem.id, elem.weight, elem.channelType)
    }
    val changedState = remember { mutableStateOf(false) }
    val choosingWeightState = remember { mutableStateOf(false) }
    Column(modifier = Modifier
            .width(INFO_MENU_WIDTH)
            .fillMaxHeight()
            .background(Color.White)
            .border(3.dp, Color.Black)
            .padding(10.dp)) {
        Text(text = "Channel")
        Spacer(Modifier
                .fillMaxWidth()
                .height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(text = "Weight:",
                    color = Color.Black,
                    modifier = Modifier.height(20.dp))
            DropdownMenu(
                    toggleModifier = Modifier.width(100.dp),
                    toggle = {
                        Row(Modifier.clickable { choosingWeightState.value = true }) {
                            Text(text = channelInfoState.value.weight.toString(), modifier = Modifier)
                            Icon(imageFromResource("arrowdown.png"))
                        }
                    },
                    expanded = choosingWeightState.value,
                    onDismissRequest = { choosingWeightState.value = false }
            ) {
                for (weight in CHANNEL_WEIGHTS) {
                    DropdownMenuItem(
                            onClick = {
                                choosingWeightState.value = false
                                changedState.value = true
                                channelInfoState.value = channelInfoState.value.copy(weight = weight)
                            }
                    ) { Text(text = weight.toString()) }
                }
                DropdownMenuItem(
                        onClick = {
                            choosingWeightState.value = false
                            changedState.value = true
                            channelInfoState.value = channelInfoState.value.copy(weight = 3, satellite = true)
                        }
                ) { Text(text = "Satellite") }
            }
        }
        Spacer(Modifier
                .fillMaxWidth()
                .height(10.dp))
        Column {
            Text("Enabled")
            Switch(checked = channelInfoState.value.enabled, onCheckedChange = {
                channelInfoState.value = channelInfoState.value.copy(enabled = it)
                changedState.value = true
            })
        }
        Spacer(Modifier
                .fillMaxWidth()
                .height(10.dp))
        Button(onClick = {
            context.infoElementState.value = elem.apply {
                weight = channelInfoState.value.weight
                channelType = channelInfoState.value.type
                satellite = channelInfoState.value.satellite
            }
            if (channelInfoState.value.enabled) {
                context.enableChannel(elem.id)
                val ch = context.infoElementState.value!! as ChannelElement
                context.graph.addLink(ch.el1.id, ch.el2.id, ch.weight)
                context.updateRouteTables()
            } else {
                context.disableChannel(elem.id)
            }
            changedState.value = false
        }, enabled = changedState.value) {
            Text(text = "Apply changes")
        }
        Spacer(Modifier.height(50.dp))
        Button(onClick = {
            context.deleteChannel(context.infoElementState.value!! as ChannelElement)
        }) {
            Text(text = "Delete")
        }
    }
}