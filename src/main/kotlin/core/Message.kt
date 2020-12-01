package core

data class Message(
        val size: Int,
        val protocol: ProtocolType,
        val packageSize: Int
)

enum class ProtocolType {
    TCP, UDP, VIRTUAL
}