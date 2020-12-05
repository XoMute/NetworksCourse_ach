package ui.appInterface.windows

import androidx.compose.desktop.AppWindow
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import colors
import ui.core.AppContext
import ui.elements.WorkstationElement
import ui.elements.base.ConnectableElement

fun RoutingTableWindow(context: AppContext) {
    val window = AppWindow(size = IntSize(350, 400))
    if (context.infoElementState.value != null) { // todo: remove
        window.show {
            MaterialTheme(colors = colors) {
                Box(modifier = Modifier.fillMaxSize()
                        .padding(10.dp)) {
                    val stateVertical = rememberScrollState(0f)
                    val stateHorizontal = rememberScrollState(0f)

                    ScrollableColumn(
                            modifier = Modifier.fillMaxSize()
                                    .padding(end = 12.dp, bottom = 12.dp),
                            scrollState = stateVertical
                    ) {
                        ScrollableRow(scrollState = stateHorizontal) {
                            val node = context.infoElementState.value as? ConnectableElement ?: return@ScrollableRow
                            val table = node.routingTable.table
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                println(table)
                                table.filter { item ->
                                    if (context.infoElementState.value!! is WorkstationElement)
                                        context.elementsState.value.find { it.id == item.key }!! is WorkstationElement
                                    else true
                                }.forEach { item ->
                                    Row {
                                        TextBox("${item.key}")
                                        TextBox("-->")
                                        TextBox("${item.value.id}")
                                    }
                                }
                            }
                        }
                    }
                    VerticalScrollbar(
                            modifier = Modifier.align(Alignment.CenterEnd)
                                    .fillMaxHeight(),
                            adapter = rememberScrollbarAdapter(stateVertical)
                    )
                }
            }
        }
    }
}