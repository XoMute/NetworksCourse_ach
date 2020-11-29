package ui.elements.base

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.IntOffset
import org.jetbrains.skija.Font
import org.jetbrains.skija.Typeface
import ui.core.DrawPageContext

interface Element {
    val id: Int
    val type: ElementType
    var pos: Offset
}

abstract class DrawableElement : Element {
    abstract val width: Int
    abstract val height: Int

    val center: Offset by lazy {
        Offset(pos.x + width / 2f, pos.y + height / 2f)
    }

    private val rect: Rect by lazy {
        Rect(topLeft = pos, bottomRight = Offset(x = pos.x + width, y = pos.y + height))
    }

    open fun collides(offset: Offset): Boolean {
        return rect.contains(offset)
    }

    protected val skiaFont: Font by lazy {
        Font(Typeface.makeDefault(), 14f)
    }

    protected val paint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.Black
        }
    }

   abstract fun draw(scope: DrawScope, context: DrawPageContext)
}

abstract class DrawableImageElement : DrawableElement() {
    abstract val image: ImageBitmap

    override fun draw(scope: DrawScope, context: DrawPageContext) {
        scope.drawImage(image, dstOffset = IntOffset(pos.x.toInt(), pos.y.toInt()))
        scope.drawIntoCanvas { canvas ->
            (canvas as DesktopCanvas).skija.drawString(id.toString(), center.x, center.y - height * 0.6f, skiaFont, paint.asFrameworkPaint())
        }
    }
}

interface ConnectableElement : Element {
    val connectionIds: MutableState<MutableSet<Int>>
}

enum class ElementType {
    WORKSTATION, COMMUNICATION_NODE, LINE
}