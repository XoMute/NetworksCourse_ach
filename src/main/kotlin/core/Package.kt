package core

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

data class Package(
        val source: Int,
        val destination: Int,
        val type: PackageType,
        val protocolType: ProtocolType,
        val size: Int,
        val ret: Boolean = false,
        var visualizationId: Int = 0) {
}

data class DrawablePackage(
        val start: Offset,
        val end: Offset,
        val protocolType: ProtocolType,
        val currentFraction: Float = 0.5f,
        val type: PackageType
) {
    fun draw(scope: DrawScope) {
        val lerp = lerp(start, end, currentFraction)
        val color = lazy { if (type == PackageType.ERROR) Color.Red else if (type == PackageType.INFO) Color.Blue else Color.Yellow }
        scope.drawCircle(color = color.value,
                radius = 10f,
                center = lerp)
        scope.drawCircle(color = Color.Black,
                radius = 11f,
                center = lerp,
                style = Stroke(2f))
    }
}

enum class PackageType {
    INFO, SERVICE, ERROR
}