package routing

data class Message(
        val size: Int,
        val protocol: ProtocolType,
        val packageSize: Int,
        val servicePackageSize: Int
) {

    fun splitIntoPackages(src: Int, dst: Int): List<Package> {
        val packages = mutableListOf<Package>()
        val n = size / packageSize
        repeat(n) {
            packages.add(Package(src, dst, PackageType.INFO, protocol, packageSize))
        }
        if (size % packageSize > 0) {
            packages.add(Package(src, dst, PackageType.INFO, protocol, size % packageSize))
        }
        return packages
    }
}

enum class ProtocolType {
    TCP, UDP, VIRTUAL
}

fun ProtocolType.directConnection(): Boolean {
    return this == ProtocolType.TCP || this == ProtocolType.VIRTUAL
}