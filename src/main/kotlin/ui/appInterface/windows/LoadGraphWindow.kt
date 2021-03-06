package ui.appInterface.windows

import androidx.compose.desktop.AppWindow
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import colors
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import serialization.Channel
import serialization.Element
import ui.core.AppContext
import ui.elements.ChannelElement
import ui.elements.CommunicationNodeElement
import ui.elements.WorkstationElement
import ui.elements.base.ConnectableElement
import java.io.File
import java.io.FileReader

fun LoadGraphWindow(context: AppContext) {
    val window = AppWindow(size = IntSize(400, 400))
    window.show {
        MaterialTheme(colors = colors) {
            val filePathState = remember { mutableStateOf("/home/xomute/graph.dmp") }
            Column(Modifier.padding(10.dp)) {
                Column(Modifier.weight(1f), Arrangement.spacedBy(5.dp)) {

                    // choose source node
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        TextField(label = { Text("Path to file:") },
                                value = filePathState.value,
                                singleLine = true,
                                onValueChange = { value: String ->
                                    filePathState.value = value
                                },
                                modifier = Modifier.fillMaxWidth())
                    }
                }
                Button(onClick = {
                    context.clear()
                    val file = File(filePathState.value)
                    if (file.exists()) {
                        val reader = FileReader(file)
                        val jsonReader = JsonReader(reader)

                        val builder = GsonBuilder()
                        val gson = builder.create()
                        // workstations
                        gson.fromJson<List<Element>>(jsonReader, object : TypeToken<List<Element>>() {}.type).forEach {
                            val workstation = WorkstationElement.deserialize(it, context)
                            context.elementsState.value = context.elementsState.value.toMutableList().also { lst -> lst.add(workstation) }
                            context.graph.addNode(workstation)
                            context.workstationIdGenerator++
                        }
                        // routers
                        gson.fromJson<List<Element>>(jsonReader, object : TypeToken<List<Element>>() {}.type).forEach {
                            val router = CommunicationNodeElement.deserialize(it, context)
                            context.elementsState.value = context.elementsState.value.toMutableList().also { lst -> lst.add(router) }
                            context.graph.addNode(router)
                            context.workstationIdGenerator++
                        }
                        // channels
                        gson.fromJson<List<Channel>>(jsonReader, object : TypeToken<List<Channel>>() {}.type).forEach {
                            val channel = ChannelElement.deserialize(it, context)
                            context.elementsState.value = context.elementsState.value.toMutableList().also { lst -> lst.add(channel) }
                            context.graph.addLink(channel.el1.id, channel.el2.id, channel.weight)
                            (channel.el1 as ConnectableElement).channels.value.add(channel)
                            (channel.el2 as ConnectableElement).channels.value.add(channel)
                            context.channelIdGenerator++
                        }
                        context.updateRouteTables()

                    } else {
                        println("No such file: $file")
                    }
                    window.close()

                }, modifier = Modifier.fillMaxWidth()) { Text(text = "Load") }
            }
        }
    }
}