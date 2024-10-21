package de.bybackfish.telosaddons.telos

import de.bybackfish.telosaddons.utils.isInRealm
import de.bybackfish.telosaddons.utils.isInShadowlands
import net.minecraft.client.MinecraftClient

enum class TelosBoss(val telosName: String, val x: Int, val y: Int, val z: Int, val shouldRender: (aliveBosses: Set<TelosBoss>) -> Boolean = {
    it.contains(this as Any) && isInRealm()
}) {

    CENTER("Center", -19, 243, 90, { true }),

    ASTAROTH("Astaroth", 253, 218, 60),
    HOLLOWBANE("Hollowbane", 231, 200, 704),
    LOTIL("Lotil", -138, 218, 20),
    ANUBIS("Anubis", 460, 204, -467),
    FREDDY("Freddy", -135, 203, 654),
    GLUMI("Glumi", 316, 200, 557),
    VALUS("Valus", 30, 210, 311),
    ILLARIUS("Illarius", 478, 217, -47),
    TIDOL("Tidol", -544, 189, 363),
    CHUNGUS("Chungus", 60, 256, -490),
    OOZUL("Oozul", -425, 195, 89),

    HERALD("Herald", 148, -47, -176, { isInShadowlands() }),
    WARDEN("Warden", -126, -46, -122, { isInShadowlands() }),
    REAPER("Reaper", 23, -47, 323, { isInShadowlands() }),
    DEFENDER("Defender", 64, -51, 64, { isInShadowlands() });


    companion object {
        fun fromName(name: String): TelosBoss? {
            return entries.find { it.telosName.equals(name, true) }
        }
    }
}