package ui.elements.base

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.IntOffset
import core.*
import org.jetbrains.skija.Font
import org.jetbrains.skija.Typeface
import ui.core.AppContext
import ui.core.RoutingTable

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

   abstract fun draw(scope: DrawScope, context: AppContext)
}

abstract class DrawableImageElement : DrawableElement() {
    abstract val image: ImageBitmap

    override fun draw(scope: DrawScope, context: AppContext) {
        scope.drawImage(image, dstOffset = IntOffset(pos.x.toInt(), pos.y.toInt()))
        scope.drawIntoCanvas { canvas ->
            (canvas as DesktopCanvas).skija.drawString(id.toString(), center.x, center.y - height * 0.6f, skiaFont, paint.asFrameworkPaint())
        }
    }
}

interface ConnectableElement : Element {
    val connectionIds: MutableState<MutableSet<Int>>
    val routingTable: RoutingTable

    fun showRoutingTable(): String {
        return routingTable.toString()
    }

    fun toGraphNode(): Node {
        return Node(id)
    }

    fun sendPackage(pkg: Package, protocol: ProtocolType): Int {
        if (pkg.destination == id) {
            log("Accepting package $pkg")
            if (protocol == ProtocolType.TCP && pkg.type == PackageType.INFO) {
                sendPackage(Package(id, pkg.source, PackageType.SERVICE, 1), protocol)
                return 1
            }
        } else {
            routingTable.table[pkg.destination]?.let {
                log("Sending package $pkg to ${it.id}")
                return it.sendPackage(pkg, protocol)
            } ?: log("Can't send package from node $id to ${pkg.destination}")
        }
        return 0
    }
}

fun ConnectableElement.log(message: String) {
    println("Node $id: $message")
}

enum class ElementType {
    WORKSTATION, COMMUNICATION_NODE, CHANNEL
}