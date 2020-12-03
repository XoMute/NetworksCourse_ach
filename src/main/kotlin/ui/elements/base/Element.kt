package ui.elements.base

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.IntOffset
import core.*
import kotlinx.coroutines.delay
import org.jetbrains.skija.Font
import org.jetbrains.skija.Typeface
import ui.core.AppContext
import ui.core.CHANNEL_DELAY
import ui.core.RoutingTable
import ui.core.SIMULATION_STEPS
import ui.elements.ChannelElement
import ui.elements.ChannelType

interface Element {
    val id: Int
    val type: ElementType
    var pos: Offset

    fun serialize(): Any {
        return serialization.Element(id, type, pos.x, pos.y)
    }
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
    val channels: MutableState<MutableSet<ChannelElement>>
    val routingTable: RoutingTable
    val packages: MutableState<MutableList<Package>>
    val acceptedPackages: MutableState<MutableList<Package>>

    fun showRoutingTable(): String {
        return routingTable.toString()
    }

    fun toGraphNode(): Node {
        return Node(id)
    }

    fun sendPackage(pkg: Package, context: AppContext): Int { // todo: implement errors
        if (pkg.destination == id) {
            log("Accepting package $pkg")
            context.packageState.value = null
            if (pkg.protocolType == ProtocolType.TCP && pkg.type == PackageType.INFO) {
                sendPackage(Package(id, pkg.source, PackageType.SERVICE, pkg.protocolType, 1), context)
                return 1
            }
            return 0
        }
        val nextNode = routingTable.table[pkg.destination]
        val channel = channels.value.find {
            it.el1.id == id && it.el2.id == nextNode?.id
                    || it.el1.id == nextNode?.id && it.el2.id == id
        }
        if (nextNode == null || channel == null) {
            log("Can't send package from node $id to ${pkg.destination}")
            return 0
        }
        nextNode.let {
            log("Sending package $pkg to ${it.id}")
            simulateDelay(channel, pkg, context)
            return it.sendPackage(pkg, context)
        }
    }

    private fun simulateDelay(channel: ChannelElement, pkg: Package, context: AppContext) {
        val delay = CHANNEL_DELAY + channel.weight
        val delayFraction = delay / SIMULATION_STEPS
        val src = if (channel.el1.id == id) channel.el1.center else channel.el2.center
        val dest = if (channel.el1.id == id) channel.el2.center else channel.el1.center
        if (channel.channelType == ChannelType.DUPLEX) {
            println("Duplex channel, waiting for $CHANNEL_DELAY")
            repeat(SIMULATION_STEPS) {
                context.packageState.value = DrawablePackage(src,
                        dest,
                        pkg.protocolType,
                        it / SIMULATION_STEPS.toFloat(),
                        pkg.type)
                Thread.sleep(delayFraction)
            }
        } else {
            println("Half duplex channel, waiting for ${CHANNEL_DELAY * 2}")
            Thread.sleep(CHANNEL_DELAY * 2)
        }
    }
}

fun ConnectableElement.log(message: String) {
    println("Node $id: $message")
}

enum class ElementType {
    WORKSTATION, COMMUNICATION_NODE, CHANNEL
}