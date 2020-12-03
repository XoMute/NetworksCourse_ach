package ui.pages.draw.windows

import androidx.compose.desktop.AppWindow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.ExperimentalKeyInput
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import core.Message
import core.ProtocolType
import ui.elements.ChannelElement
import ui.elements.base.ConnectableElement

data class MessageInfoState(
        val fromNode: ConnectableElement? = null,
        val toNode: ConnectableElement? = null,
        val packageSize: Int = 10,
        val messageSize: Int = 10,
        val protocolType: ProtocolType = ProtocolType.TCP
)

@OptIn(ExperimentalKeyInput::class)
fun SendMessageWindow(
        nodes: List<ConnectableElement>,
        channels: List<ChannelElement>,
        onSendAction: (Int, Int, Message) -> Unit
) {
    val window = AppWindow(size = IntSize(400, 400)).also {
        it.keyboard.setShortcut(Key.Escape) {
            it.close()
        }
    }
    window.show {
        val sourceNodeState = remember { mutableStateOf(false) }
        val destinationNodeState = remember { mutableStateOf(false) }
        val messageInfoState = remember { mutableStateOf(MessageInfoState()) }
        val errorSourceState = remember { mutableStateOf(false) }
        val errorDestinationState = remember { mutableStateOf(false) }
        Column(Modifier.padding(10.dp)) {
            Column(Modifier.weight(1f), Arrangement.spacedBy(5.dp)) {

                // choose source node
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(text = "From workstation:",
                            color = if (errorSourceState.value) Color.Red else Color.Black,
                            modifier = Modifier.width(150.dp))
                    DropdownMenu(
                            toggle = {
                                Text(text = messageInfoState.value.fromNode?.id?.toString()
                                        ?: "Select station", modifier = Modifier
                                        .clickable { sourceNodeState.value = true })
                            },
                            expanded = sourceNodeState.value,
                            onDismissRequest = { sourceNodeState.value = false }
                    ) {
                        for (node in nodes) {
                            DropdownMenuItem(
                                    onClick = {
                                        errorSourceState.value = false
                                        sourceNodeState.value = false
                                        messageInfoState.value = messageInfoState.value.copy(fromNode = node)
                                    }
                            ) { Text(text = node.id.toString()) }
                        }
                    }
                }

                // choose destination node
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(text = "To workstation:",
                            color = if (errorDestinationState.value) Color.Red else Color.Black,
                            modifier = Modifier.width(150.dp))
                    DropdownMenu(
                            toggle = {
                                Text(text = messageInfoState.value.toNode?.id?.toString()
                                        ?: "Select station", modifier = Modifier
                                        .clickable { destinationNodeState.value = true })
                            },
                            expanded = destinationNodeState.value,
                            onDismissRequest = { destinationNodeState.value = false }
                    ) {
                        for (node in nodes) {
                            DropdownMenuItem(
                                    onClick = {
                                        errorDestinationState.value = false
                                        destinationNodeState.value = false
                                        messageInfoState.value = messageInfoState.value.copy(toNode = node)
                                    }
                            ) { Text(text = node.id.toString()) }
                        }
                    }
                }

                // choose package size
                Row {
                    TextField(value = messageInfoState.value.packageSize.toString(),
                            label = {
                                Text(text = "Package size:")
                            },
                            singleLine = true,
                            onValueChange = { value ->
                                messageInfoState.value = messageInfoState.value.copy(
                                        packageSize = value.toIntOrNull() ?: messageInfoState.value.packageSize)
                            })
                }
                // choose message size
                Row {
                    TextField(value = messageInfoState.value.messageSize.toString(),
                            label = {
                                Text(text = "Message size:")
                            },
                            singleLine = true,
                            onValueChange = { value ->
                                messageInfoState.value = messageInfoState.value.copy(
                                        messageSize = value.toIntOrNull() ?: messageInfoState.value.messageSize)
                            })
                }
                // choose connection type
                Column {
                    Text(text = "Connection type:")
                    Spacer(Modifier.height(10.dp))
                    Column {
                        Row {
                            TextBox("Logical")
                            Checkbox(checked = messageInfoState.value.protocolType == ProtocolType.TCP, onCheckedChange = {
                                messageInfoState.value = messageInfoState.value.copy(protocolType = ProtocolType.TCP)
                            })
                        }
                        Row {
                            TextBox("Virtual")
                            Checkbox(checked = messageInfoState.value.protocolType == ProtocolType.VIRTUAL, onCheckedChange = {
                                messageInfoState.value = messageInfoState.value.copy(protocolType = ProtocolType.VIRTUAL)
                            })
                        }
                        Row {
                            TextBox("Datagram")
                            Checkbox(checked = messageInfoState.value.protocolType == ProtocolType.UDP, onCheckedChange = {
                                messageInfoState.value = messageInfoState.value.copy(protocolType = ProtocolType.UDP)
                            })
                        }
                    }
                }
            }
            Button(onClick = {
                when {
                    messageInfoState.value.fromNode == null -> {
                        errorSourceState.value = true
                    }
                    messageInfoState.value.toNode == null -> {
                        errorDestinationState.value = true
                    }
                    else -> {
                        errorSourceState.value = false
                        errorDestinationState.value = false
                        val message = Message(
                                size = messageInfoState.value.messageSize,
                                protocol = messageInfoState.value.protocolType,
                                packageSize = messageInfoState.value.packageSize
                        )
                        onSendAction(
                                messageInfoState.value.fromNode!!.id,
                                messageInfoState.value.toNode!!.id,
                                message)
                        window.close()
                    }
                }

            }, modifier = Modifier.fillMaxWidth()) { Text(text = "Send") }
        }
    }
}

@Composable
fun TextBox(text: String = "") {
    Box(
            modifier = Modifier.height(32.dp)
//                    .background(Color(200, 0, 0, 20))
                    .width(100.dp)
                    .padding(start = 2.dp),
            contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text)
    }
}