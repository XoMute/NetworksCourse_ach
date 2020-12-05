package ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ui.core.AppContext
import ui.appInterface.DrawPage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainUI() {

    Column(
            modifier = Modifier.fillMaxSize()
    ) {
        val context = AppContext()
        DrawPage(context)
    }
}
