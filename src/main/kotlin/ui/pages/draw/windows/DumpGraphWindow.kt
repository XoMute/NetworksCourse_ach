package ui.pages.draw.windows

import androidx.compose.desktop.AppWindow
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.google.gson.GsonBuilder
import ui.elements.ChannelElement
import ui.elements.CommunicationNodeElement
import ui.elements.WorkstationElement
import ui.elements.base.ConnectableElement
import ui.elements.base.Element
import java.io.File
import java.io.FileWriter

fun DumpGraphWindow(elements: List<Element>) {
    val window = AppWindow(size = IntSize(400, 400))
    window.show {
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
                val file = File(filePathState.value)
                if (file.createNewFile() || file.exists()) {
                    val builder = GsonBuilder()
                    val gson = builder.create()
                    println(gson.toJson(elements.map { it.serialize() }))
                    val writer = FileWriter(file)
                    writer.write(gson.toJson(elements.filterIsInstance<WorkstationElement>().map { it.serialize() }))
                    writer.write(gson.toJson(elements.filterIsInstance<CommunicationNodeElement>().map { it.serialize() }))
                    writer.write(gson.toJson(elements.filterIsInstance<ChannelElement>().map { it.serialize() }))
                    writer.close()
                } else {
                    println("Can't create file: $file")
                }
                window.close()

            }, modifier = Modifier.fillMaxWidth()) { Text(text = "Dump") }
        }
    }
}