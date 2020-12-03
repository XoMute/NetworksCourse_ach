package ui.elements.controlPanel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.unit.dp

@Composable
fun ControlPanelElement(imagePath: String, onClick: (() -> Unit)) {
    Image(imageFromResource(imagePath), Modifier
            .fillMaxHeight()
            .width(64.dp)
            .background(Color.Transparent)
            .clickable { onClick() })
}