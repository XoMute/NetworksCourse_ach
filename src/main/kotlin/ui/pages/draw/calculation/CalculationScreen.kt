package ui.pages.draw.calculation

import androidx.compose.desktop.AppWindow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.ExperimentalKeyInput
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ui.elements.ChannelElement
import ui.elements.base.ConnectableElement

data class CalculationState(
        val fromNode: ConnectableElement? = null,
        val toNode: ConnectableElement? = null
)

@OptIn(ExperimentalKeyInput::class)
fun SendMessageWindow(
        nodes: List<ConnectableElement>,
        lines: List<ChannelElement>,
        onCalculated: () -> Unit
) {
    val window = AppWindow(size = IntSize(400, 400)).also {
        it.keyboard.setShortcut(Key.Escape) {
            it.close()
        }
    }
    window.show {
        val workstationToDropDownState = remember { mutableStateOf(false) } // todo: rename
        val workstationFromDropDownState = remember { mutableStateOf(false) }
        val calcState = remember { mutableStateOf(CalculationState()) }
        val errorFromState = remember { mutableStateOf(false) }
        Column(Modifier.padding(10.dp)) {
            Column(Modifier.weight(1f)) {
                Row {
                    Text(text = "From workstation:", color = if (errorFromState.value) Color.Red else Color.Black)
                    Spacer(modifier = Modifier.height(1.dp).width(5.dp))
                    DropdownMenu(
                            toggleModifier = Modifier.wrapContentSize(),
                            dropdownModifier = Modifier.wrapContentSize(),
                            toggle = {
                                Text(calcState.value.fromNode?.id?.toString() ?: "Select station", modifier = Modifier
                                        .clickable { workstationFromDropDownState.value = true })
                            },
                            expanded = workstationFromDropDownState.value,
                            onDismissRequest = { workstationFromDropDownState.value = false }
                    ) {
                        //todo this must be fixed in further build of compose
                        for (node in nodes) {
                            DropdownMenuItem(
                                    onClick = {
                                        errorFromState.value = false
                                        workstationFromDropDownState.value = false
                                        calcState.value = calcState.value.copy(fromNode = node)
                                    }
                            ) { Text(text = node.id.toString()) }
                        }
                    }
                }
                Row {
                    Text(text = "To workstation:")
                    Spacer(modifier = Modifier.height(1.dp).width(5.dp))
                    DropdownMenu(
                            toggleModifier = Modifier.wrapContentSize(),
                            dropdownModifier = Modifier.wrapContentSize(),
                            toggle = {
                                Text(calcState.value.toNode?.id?.toString() ?: "All", modifier = Modifier
                                        .clickable { workstationToDropDownState.value = true })
                            },
                            expanded = workstationToDropDownState.value,
                            onDismissRequest = { workstationToDropDownState.value = false }
                    ) {
                        //todo this must be fixed in further build of compose
                        for (node in nodes) {
                            DropdownMenuItem(
                                    onClick = {
                                        workstationToDropDownState.value = false
                                        calcState.value = calcState.value.copy(fromNode = node)
                                    }
                            ) { Text(text = node.id.toString()) }
                        }
                    }
                }
            }
            Button(onClick = {
                if (calcState.value.fromNode == null) {
                    errorFromState.value = true
                } else {
                    errorFromState.value = false
//                    onCalculated(onCalculateClicked(
//                            workstations = nodes.map { it.mapToAlgorithmEntity() },
//                            lines = lines.map { it.mapToAlgorithmEntity() },
//                            from = calcState.value.fromNode!!.mapToAlgorithmEntity(),
//                            to = calcState.value.toNode?.mapToAlgorithmEntity()
//                    ))
                    window.close()
                }

            }, modifier = Modifier.fillMaxWidth()) { Text(text = "Send") }
        }
    }
}

//private fun onCalculateClicked(
//        workstations: List<Workstation>,
//        lines: List<Line>,
//        from: Workstation,
//        to: Workstation?
//): PathCalculationResult {
//    val bellmanFordAlgorithm = BellmanFordAlgorithm(workstations, lines)
//    if (to == null) {
//        return PathCalculationResult(
//                paths = bellmanFordAlgorithm.calculate(from = from)
//        )
//    }
//    return PathCalculationResult(
//            paths = bellmanFordAlgorithm.calculate(from = from, to = to)
//    )
//}