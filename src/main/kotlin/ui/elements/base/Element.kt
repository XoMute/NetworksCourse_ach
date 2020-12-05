package ui.elements.base

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.IntOffset
import routing.*
import org.jetbrains.skija.Font
import org.jetbrains.skija.Typeface
import ui.core.*
import ui.elements.ChannelElement
import ui.elements.ChannelType
import java.lang.RuntimeException
import kotlin.random.Random

interface Element {
    val id: Int
    val type: ElementType
    var pos: Offset
    var enabled: Boolean

    fun serialize(): Any {
        return serialization.Element(id, type, pos.x, pos.y)
    }
}

abstract class DrawableElement : Element {
    abstract val width: Int
    abstract val height: Int

    val center: Offset
        get() {
            return Offset(pos.x + width / 2f, pos.y + height / 2f)
        }

    private val rect: Rect
        get() {
            return Rect(topLeft = pos, bottomRight = Offset(x = pos.x + width, y = pos.y + height))
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
        scope.drawImage(image, dstOffset = IntOffset(pos.x.toInt(), pos.y.toInt()),
                colorFilter = if (!enabled) ColorFilter(Color.Gray, BlendMode.Color) else null)
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

    fun toGraphNode(): Node {
        return Node(id)
    }

    /**
     * returns: number of service packages returned
     */
    fun sendPackage(pkg: Package, context: AppContext): Int {
        if (pkg.destination == id) {
            context.packagesState[pkg.visualizationId] = null
            if (pkg.protocolType.directConnection() && pkg.type == PackageType.INFO) {
                sendPackage(Package(id, pkg.source, PackageType.SERVICE, pkg.protocolType, 1, true), context)
                return 1
            }
            if (pkg.type == PackageType.ERROR) {
                throw ChannelErrorException()
            }
            return 0
        }
        if (context.visualSimulationState.value) {
            while (true) {
                if (context.playSimulationState.value) {
                    break
                } else if (context.stepCountState.value > 0) {
                    context.stepCountState.value = context.stepCountState.value - 1
                    break
                }

                if (!context.visualSimulationState.value) { // simulation was fast-forwarded
                    return sendPackage(pkg, context)
                }
                Thread.sleep(100)
            }
        }
        val nextNode = routingTable.table[pkg.destination]
        val channel = channels.value.find {
            it.el1.id == id && it.el2.id == nextNode?.id
                    || it.el1.id == nextNode?.id && it.el2.id == id
        }
        if (nextNode == null || channel == null || !channel.el1.enabled || !channel.el2.enabled) {
            throw NoConnectionException("Can't send package from node $id to ${pkg.destination}")
        }

        if (context.visualSimulationState.value) {

            channel.highlightedState.value = true
            if (pkg.protocolType.directConnection() && pkg.type == PackageType.INFO && Random.nextDouble() < channel.errorProbability) {
                simulateVisualDelay(channel, pkg, context)
                nextNode.sendPackage(Package(id, pkg.source, PackageType.ERROR, pkg.protocolType, 1, true), context)
                return 1
            }
            simulateVisualDelay(channel, pkg, context)
            return nextNode.sendPackage(pkg, context)
        } else {
            channel.highlightedState.value = true
            if (pkg.protocolType.directConnection() && pkg.type == PackageType.INFO && Random.nextDouble() < channel.errorProbability) {
                simulateDelay(channel)
                nextNode.sendPackage(Package(id, pkg.source, PackageType.ERROR, pkg.protocolType, 1, true), context)
                return 1
            }
            nextNode.let {
                simulateDelay(channel)
                return it.sendPackage(pkg, context)
            }
        }
    }

    private fun simulateDelay(channel: ChannelElement) {
        var delay = CHANNEL_DELAY + channel.weight
        if (channel.channelType == ChannelType.DUPLEX) {
            Thread.sleep(0L, delay)
        } else {
            delay *= 2
            Thread.sleep(0L, delay)
        }
    }

    private fun simulateVisualDelay(channel: ChannelElement, pkg: Package, context: AppContext) {
        var delay = SIMULATION_DELAY + channel.weight
        var delayFraction = delay / SIMULATION_STEPS
        val src = if (channel.el1.id == id) channel.el1.center else channel.el2.center
        val dest = if (channel.el1.id == id) channel.el2.center else channel.el1.center
        // create drawable package for current channel using state map
        context.packagesState[pkg.visualizationId] = DrawablePackage(src,
                dest,
                pkg.protocolType,
                0f,
                pkg.type)
        if (channel.channelType == ChannelType.DUPLEX) {
            repeat(SIMULATION_STEPS) {
                context.packagesState[pkg.visualizationId] = context.packagesState[pkg.visualizationId]?.copy(
                        currentFraction = it / SIMULATION_STEPS.toFloat()
                )
                Thread.sleep(delayFraction)
            }
        } else {
            delay *= 2
            delayFraction = delay / SIMULATION_STEPS
            repeat(SIMULATION_STEPS) {
                context.packagesState[pkg.visualizationId] = context.packagesState[pkg.visualizationId]?.copy(
                        currentFraction = it / SIMULATION_STEPS.toFloat()
                )
                Thread.sleep(delayFraction)
            }
        }
    }
}

class NoConnectionException(val msg: String) : RuntimeException(msg)
class ChannelErrorException : RuntimeException()

fun ConnectableElement.log(message: String) {
    println("Node $id: $message")
}

enum class ElementType {
    WORKSTATION, COMMUNICATION_NODE, CHANNEL
}