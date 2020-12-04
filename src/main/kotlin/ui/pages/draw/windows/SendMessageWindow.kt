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
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import colors
import core.Message
import core.ProtocolType
import ui.core.AppContext
import ui.elements.ChannelElement
import ui.elements.WorkstationElement
import ui.elements.base.ConnectableElement

data class MessageInfoState(
        val fromNode: ConnectableElement? = null,
        val toNode: ConnectableElement? = null,
        val packageSize: Int = 10,
        val servicePackageSize: Int = 1,
        val messageSize: Int = 10,
        val protocolType: ProtocolType = ProtocolType.TCP
)

fun SendMessageWindow(
        nodes: List<ConnectableElement>,
        context: AppContext,
        onSendAction: (Int, Int, Message) -> Unit,
) {
    val window = AppWindow(size = IntSize(450, 600))
    window.show {
        MaterialTheme(colors = colors) {
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
                                modifier = Modifier.width(180.dp))
                        DropdownMenu(
                                toggle = {
                                    Row(Modifier.clickable { sourceNodeState.value = true }) {
                                        Text(text = messageInfoState.value.fromNode?.id?.toString() ?: "Select station")
                                        Icon(imageFromResource("arrowdown.png"))
                                    }
                                },
                                expanded = sourceNodeState.value,
                                onDismissRequest = { sourceNodeState.value = false }
                        ) {
                            for (node in nodes.filterIsInstance<WorkstationElement>().filter { it.enabled }) {
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
                                modifier = Modifier.width(180.dp))
                        DropdownMenu(
                                toggle = {
                                    Row(Modifier.clickable { destinationNodeState.value = true }) {
                                        Text(text = messageInfoState.value.toNode?.id?.toString() ?: "Select station")
                                        Icon(imageFromResource("arrowdown.png"))
                                    }
                                },
                                expanded = destinationNodeState.value,
                                onDismissRequest = { destinationNodeState.value = false }
                        ) {
                            for (node in nodes.filterIsInstance<WorkstationElement>().filter { it.enabled }) {
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
                    // choose service package size
                    Row {
                        TextField(value = messageInfoState.value.servicePackageSize.toString(),
                                label = {
                                    Text(text = "Service package size:")
                                },
                                singleLine = true,
                                onValueChange = { value ->
                                    messageInfoState.value = messageInfoState.value.copy(
                                            servicePackageSize = value.toIntOrNull()
                                                    ?: messageInfoState.value.servicePackageSize)
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
                            context.visualSimulationState.value = false
                            context.removeHighlight()
                            val message = Message(
                                    size = messageInfoState.value.messageSize,
                                    protocol = messageInfoState.value.protocolType,
                                    packageSize = messageInfoState.value.packageSize,
                                    servicePackageSize = messageInfoState.value.servicePackageSize
                            )
                            onSendAction(
                                    messageInfoState.value.fromNode!!.id,
                                    messageInfoState.value.toNode!!.id,
                                    message)
                            window.close()
                        }
                    }

                }, modifier = Modifier.fillMaxWidth()) { Text(text = "Send") }
                Spacer(Modifier.height(5.dp))
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
                            context.removeHighlight()
                            context.startVisualSimulation()
                            val message = Message(
                                    size = messageInfoState.value.messageSize,
                                    protocol = messageInfoState.value.protocolType,
                                    packageSize = messageInfoState.value.packageSize,
                                    servicePackageSize = messageInfoState.value.servicePackageSize
                            )
                            onSendAction(
                                    messageInfoState.value.fromNode!!.id,
                                    messageInfoState.value.toNode!!.id,
                                    message)
                            window.close()
                        }
                    }

                }, modifier = Modifier.fillMaxWidth()) { Text(text = "Simulate") }
            }
        }
    }
}

@Composable
fun TextBox(text: String = "") {
    Box(
            modifier = Modifier.height(32.dp)
                    .width(100.dp)
                    .padding(start = 2.dp),
            contentAlignment = Alignment.Center
    ) {
        Text(text = text, textAlign = TextAlign.Center)
    }
}