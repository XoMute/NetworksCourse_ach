package core

data class Package(
        val source: Int,
        val destination: Int,
        val type: PackageType,
        val size: Int) {
}

enum class PackageType {
    INFO, SERVICE
}