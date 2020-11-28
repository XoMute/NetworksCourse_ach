package ui.elements.base

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.jetbrains.skija.Font
import org.jetbrains.skija.Typeface

interface Element {
    val id: Int
    val type: ElementType
    var pos: Offset
    val connectable: Boolean

    fun collides(offset: Offset): Boolean
    fun draw(scope: DrawScope)
}

abstract class ConnectableElement : Element {
    abstract val connectionIds: MutableState<MutableSet<Int>>
    override val connectable: Boolean
        get() = true

    abstract val width: Int
    abstract val height: Int

    val center: Offset by lazy {
        Offset(pos.x + width / 2f, pos.y + height / 2f)
    }

    private val rect: Rect by lazy {
        Rect(topLeft = pos, bottomRight = Offset(x = pos.x + width, y = pos.y + height))
    }

    override fun collides(offset: Offset): Boolean {
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
}

enum class ElementType {
    WORKSTATION, COMMUNICATION_NODE, CONNECTION
}