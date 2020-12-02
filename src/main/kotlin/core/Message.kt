package core

data class Message(
        val size: Int,
        val protocol: ProtocolType,
        val packageSize: Int
) {

    fun splitIntoPackages(src: Int, dst: Int, pkgType: PackageType = PackageType.INFO): List<Package> { // todo: remove package type argument
        val packages = mutableListOf<Package>()
        val n = size / packageSize
        repeat(n) {
            packages.add(Package(src, dst, pkgType, packageSize))
        }
        if (size % packageSize > 0) {
            packages.add(Package(src, dst, pkgType, size % packageSize))
        }
        return packages
    }
}

enum class ProtocolType {
    TCP, UDP, VIRTUAL
}