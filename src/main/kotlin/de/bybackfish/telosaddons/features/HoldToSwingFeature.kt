package de.bybackfish.telosaddons.features

import de.bybackfish.telosaddons.core.annotations.Category
import de.bybackfish.telosaddons.core.annotations.Property
import de.bybackfish.telosaddons.core.event.Subscribe
import de.bybackfish.telosaddons.core.feature.Feature
import de.bybackfish.telosaddons.events.ClientTickEvent
import net.minecraft.util.Hand

@Category("Combat")
class HoldToSwingFeature: Feature() {

    @Property
        (description = "Wait for the mainhand cooldown to finish before using the mainhand")
    var waitForMainhandCooldown = false

    @Property
        (description = "Wait for the offhand cooldown to finish before using the offhand")
    var waitForOffhandCooldown = true

    @Property
        (description = "Use mainhand whilst holding down the attack button")
    var mainHand = true

    @Property
        (description = "Use offhand whilst holding down the attack button")
    var offhand = false

    @Subscribe
    fun onTick(event: ClientTickEvent) {
        if(player == null) return
        if(!mc.options.attackKey.isPressed) return

        if (mainHand && (!waitForMainhandCooldown || !player!!.itemCooldownManager.isCoolingDown(player!!.mainHandStack.item)))
            player!!.swingHand(Hand.MAIN_HAND)

        if (offhand && (!waitForOffhandCooldown || !player!!.itemCooldownManager.isCoolingDown(player!!.offHandStack.item)))
            mc.interactionManager!!.interactItem(player, Hand.OFF_HAND)
    }

}