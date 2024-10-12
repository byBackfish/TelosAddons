package de.bybackfish.telosaddons.telos

enum class BagType(val customModelData: Int = 0) {
    PINK,
    PURPLE,
    CYAN,
    BLUE,
    RED,
    GOLD(4),
    GREEN(7),
    WHITE(2),
    BLACK(3),
    ORANGE,
    RELIC(8),
    CROSS(5),
    OTHER;


    companion object {
        fun fromCustomModelData(customModelData: Int): BagType? {
            return entries.filter{
                it.customModelData != 0
            }.firstOrNull { it.customModelData == customModelData }
        }
    }
}