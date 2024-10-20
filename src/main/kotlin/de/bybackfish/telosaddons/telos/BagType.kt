package de.bybackfish.telosaddons.telos

enum class BagType(val droppedModelData: Int, val totemModelData: Int = 0) {
    YELLOW(829116),
    ORANGE(829117),
    BROWN(829118),
    LIGHT_BROWN(829119),
    PINK(829120),
    PELUTE(829121),
    TURQUOISE(829122),
    CYAN(829123),
    LIGHT_BLUE(829124),
    DARK_BLUE(829125),
    RUNE(829139, 14),
    GOLD(829142, 15),
    GREEN(829126, 13),
    WHITE(829127, 11),
    BLACK(829128, 10),
    UNHOLY(829129, 12),
    HALLOWEEN(829137, 9),
    OTHER(-1, -1);


    companion object {
        fun fromTotemModelData(totemModelData: Int): BagType? {
            return entries.filter{
                it.totemModelData != 0
            }.firstOrNull { it.totemModelData == totemModelData }
        }

        fun fromDroppedModelData(droppedModelData: Int): BagType? {
            return entries.firstOrNull { it.droppedModelData == droppedModelData }
        }
    }
}