package de.bybackfish.telosaddons.features

import de.bybackfish.telosaddons.core.annotations.Category
import de.bybackfish.telosaddons.core.annotations.Property
import de.bybackfish.telosaddons.core.event.Subscribe
import de.bybackfish.telosaddons.core.feature.Feature
import de.bybackfish.telosaddons.core.lastFakedBag
import de.bybackfish.telosaddons.events.telos.BagDropEvent
import de.bybackfish.telosaddons.telos.BagType
import gg.essential.vigilance.data.PropertyType

@Category("Fun")
class BagFakeFeature: Feature() {

    @Property(
        sortingOrder = 1,
        description = "Fake",
        forceType = PropertyType.SELECTOR,
        options = ["None", "Random UT", "Royal Bag", "Bloodshot Bag", "Companion Bag", "Unholy Bag", "Rune Bag"]
    )
    var fakeType = 0

    @Property
    var addToCounter = false

    private fun getSelectedBag(): BagType? {
        return when(fakeType) {
            1 -> {
                val randomBag = BagType.entries.filter { it.totemModelData != -1 }.random()
                randomBag
            }
            2 -> BagType.WHITE
            3 -> BagType.BLACK
            4 -> BagType.GOLD
            5 -> BagType.UNHOLY
            6 -> BagType.RUNE
            else -> null
        }
    }

    @Subscribe(priority = 100)
    fun onBagDrop(event: BagDropEvent) {
        println("Bag drop event | Bag fake feature")
        val fakeBag = getSelectedBag() ?: return
        event.bag = fakeBag

        val fakeItemStack = fakeBag.createFakeDroppedItemStack()
        event.itemRef.set(fakeItemStack)

        fakeBag.playSounds()

        if(!addToCounter) {
            lastFakedBag = System.currentTimeMillis()
        }
        mc.gameRenderer.showFloatingItem(fakeBag.createFakeTotemItemStack())
    }

}