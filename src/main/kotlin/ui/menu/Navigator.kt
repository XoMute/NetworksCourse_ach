package ui.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import ui.core.DrawPageContext
import ui.pages.draw.DrawPage
import ui.pages.routing.RoutingPage

@Composable
fun Navigator(navState: MutableState<Pair<Tab, Any?>>) {
    val args = navState.value.second
    when (navState.value.first) {
        Tab.DRAW -> {
            DrawPage(args as? DrawPageContext ?: DrawPageContext()) { tab: Tab, arg: Any? ->
                navigateTo(navState, tab, arg)
            }
        }
    }
}

fun navigateTo(state: MutableState<Pair<Tab, Any?>>, tabType: Tab, args: Any? = null) {
    state.value = tabType to args
}