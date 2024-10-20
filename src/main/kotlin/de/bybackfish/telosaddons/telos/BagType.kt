package de.bybackfish.telosaddons.telos

enum class BagType(val customModelData: Int = 0) {
    PINK,
    PURPLE,
    CYAN,
    BLUE,
    RED,
    GOLD(15),
    GREEN(13),
    WHITE(11),
    BLACK(10),
    ORANGE(9),
    RELIC,
    CROSS(12),
    RUNE(14),
    OTHER;


    companion object {
        fun fromCustomModelData(customModelData: Int): BagType? {
            return entries.filter{
                it.customModelData != 0
            }.firstOrNull { it.customModelData == customModelData }
        }
    }
}