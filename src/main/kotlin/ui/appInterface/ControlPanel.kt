package ui.appInterface

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.unit.dp
import ui.core.AppContext
import ui.core.CHANNEL_WEIGHTS
import ui.elements.controlPanel.ControlPanelElement
import ui.elements.base.ElementType

@Composable
fun ControlPanel(context: AppContext) {
    Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .height(CONTROL_PANEL_HEIGHT)
                    .border(BorderStroke(2.dp, SolidColor(Color.Black)))
    ) {
        val chosingWeightState = remember { mutableStateOf(false) }
        Row(modifier = Modifier
                .padding(start = 50.dp, top = 5.dp, bottom = 5.dp)
                .align(Alignment.Start)) {
            ControlPanelElement("workstation.png") {
                context.selectedTypeState.value = ElementType.WORKSTATION
            }
            Spacer(modifier = Modifier
                    .width(32.dp))
            ControlPanelElement("router.png") {
                context.selectedTypeState.value = ElementType.COMMUNICATION_NODE
            }
            Spacer(modifier = Modifier
                    .width(32.dp))
            ControlPanelElement("line.png") {
                context.selectedTypeState.value = ElementType.CHANNEL
            }
            Spacer(modifier = Modifier
                    .width(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Channel weight:",
                        color = Color.Black,
                        modifier = Modifier.height(20.dp))
                DropdownMenu(
                        toggleModifier = Modifier.width(120.dp),
                        toggle = {
                            Row(Modifier.clickable { chosingWeightState.value = true }) {
                                Text(text = context.channelWeightState.value, modifier = Modifier)
                                Icon(imageFromResource("arrowdown.png"))
                            }
                        },
                        expanded = chosingWeightState.value,
                        onDismissRequest = { chosingWeightState.value = false }
                ) {
                    for (weight in CHANNEL_WEIGHTS) {
                        DropdownMenuItem(
                                onClick = {
                                    chosingWeightState.value = false
                                    context.satelliteChannelState.value = false
                                    context.channelWeightState.value = weight.toString()
                                }
                        ) { Text(text = weight.toString()) }
                    }
                    DropdownMenuItem(
                            onClick = {
                                chosingWeightState.value = false
                                context.satelliteChannelState.value = false
                                context.channelWeightState.value = "Random"
                            }
                    ) { Text(text = "Random") }
                    DropdownMenuItem(
                            onClick = {
                                chosingWeightState.value = false
                                context.channelWeightState.value = "3"
                                context.satelliteChannelState.value = true
                            }
                    ) { Text(text = "Satellite") }
                }
            }

            Spacer(modifier = Modifier
                    .width(100.dp))
            Column(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.width(200.dp)) {
                Button(onClick = { context.sendMessage() }, modifier = Modifier.fillMaxWidth(), enabled = context.sendMessageButtonState.value) {
                    Text(text = "Send Message")
                }
            }
            if (context.visualSimulationState.value) {
                Spacer(Modifier.width(5.dp))
                Column(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.width(200.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Button(onClick = { context.playSimulationState.value = true }, modifier = Modifier.width(50.dp)) {
                            Icon(imageFromResource("playicon.png"))
                        }
                        Button(onClick = { context.playSimulationState.value = false }, modifier = Modifier.width(50.dp)) {
                            Icon(imageFromResource("pauseicon.png"))
                        }
                        Button(onClick = { context.stopVisualSimulation() }, modifier = Modifier.width(85.dp)) {
                            Text("FF")
                        }
                    }
                    Button(onClick = { context.stepCountState.value++ }, modifier = Modifier.width(195.dp), enabled = !context.playSimulationState.value) {
                        Text(text = "Step")
                    }
                }
                Spacer(Modifier.width(95.dp))
            } else {
                Spacer(Modifier.width(300.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.width(200.dp)) {
                Button(onClick = { context.dumpGraph() }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Dump graph")
                }
                Spacer(modifier = Modifier
                        .width(100.dp))
                Button(onClick = { context.loadGraph() }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Load graph")
                }
            }
            Spacer(modifier = Modifier
                    .width(100.dp))
            Column(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.width(100.dp)) {
                Button(onClick = { context.clear() }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Clear")
                }
            }
        }
    }
}