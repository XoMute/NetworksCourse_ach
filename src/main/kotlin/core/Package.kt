package core

data class Package(
        val source: Int,
        val destination: Int,
        val type: PackageType,
        val protocolType: ProtocolType,
        val size: Int) {
}

enum class PackageType {
    INFO, SERVICE
}