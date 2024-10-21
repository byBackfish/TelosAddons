package de.bybackfish.telosaddons.telos

enum class TelosBoss(val telosName: String, val x: Int, val y: Int, val z: Int, val permanent: Boolean = false) {

    SHADOWLANDS_SPAWN("Shadowlands Spawn", 170, 61, 427, true),
    CENTER("Center", -19, 500, 90, true),

    HERALD("Herald", 148, -47, -176),
    WARDEN("Warden", -126, -46, -122),
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
    REAPER("Reaper", 23, -47, 323),
    DEFENDER("Defender", 64, -51, 64);

    companion object {
        fun fromName(name: String): TelosBoss? {
            return entries.find { it.telosName.equals(name, true) }
        }
    }
}