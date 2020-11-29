package ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp

@Composable
fun Tab(
        text: String,
        modifier: Modifier = Modifier,
        selected: Boolean = false,
        onClick: () -> Unit = {}
) {
    Surface(modifier = modifier
            .border(1.dp, Color.Blue, RoundedCornerShape(topLeft = 10.dp, topRight = 10.dp))
            .background(Color.LightGray)
            .clickable { onClick() },
            color = if (selected) Color.DarkGray else Color.White,
            shape = RoundedCornerShape(topLeft = 10.dp, topRight = 10.dp)) {
        Text(text = text,
             modifier = Modifier.wrapContentSize(align = Alignment.Center),
             fontStyle = if (selected) FontStyle.Italic else FontStyle.Normal)
    }
}

enum class Tab {
    DRAW, ROUTING
}