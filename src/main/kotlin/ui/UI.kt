package ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.TabRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ui.core.DrawPageContext
import ui.menu.Navigator
import ui.menu.Tab
import ui.menu.navigateTo
import ui.pages.draw.DrawPage

const val DRAW_PAGE_TITLE = "Draw"
const val ROUTING_PAGE_TITLE = "Routing"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainUI() {

    val navigationState: MutableState<Pair<Tab, Any?>> = remember { mutableStateOf(Tab.DRAW to null) }

    Column(
            modifier = Modifier.fillMaxSize()
    ) {
        Row(modifier = Modifier
                .fillMaxWidth(fraction = 1f)
                .background(Color.LightGray)
                .wrapContentHeight(align = Alignment.Top)) {
            Tab(
                    modifier = Modifier.weight(1 / 2f),
                    text = DRAW_PAGE_TITLE,
                    selected = navigationState.value.first == Tab.DRAW,
            ) {
                navigateTo(navigationState, Tab.DRAW)
            }
            Tab(
                    modifier = Modifier.weight(1 / 2f),
                    text = ROUTING_PAGE_TITLE,
                    selected = navigationState.value.first == Tab.ROUTING,
            ) {
                navigateTo(navigationState, Tab.ROUTING)
            }
        }
        Navigator(navigationState)
    }
}
