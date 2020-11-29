package ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ui.core.DrawPageContext
import ui.pages.draw.DrawPage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainUI() {
    Column(
            modifier = Modifier.fillMaxSize()
                    .background(color = Color.White)
    ) {
        val context = DrawPageContext()
        DrawPage(context)
    }
}