package serialization

import ui.elements.ChannelType

data class Channel(val id: Int, val el1: Int, val el2: Int, val weight: Int, val type: ChannelType, val errorProbability: Float)