package de.bybackfish.telosaddons

import VERSION
import gg.essential.universal.UChat
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.*
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Rarity
import java.awt.Color
import java.awt.Desktop
import java.io.File

class TelosConfig : Vigilant(
    File("./config/TelosAddons/telosaddons.toml"),
    "TelosAddons",
    sortingBehavior = CustomSortingBehavior(),
    propertyCollector = TelosAddons.propertyCollector
) {

    @Property(
        type = PropertyType.CHECKBOX,
        name = "TelosAddons $VERSION",
        description = "Coming soonTM\n\nMade by byBackfish",
        category = "General"
    )
    val _telos = true

    @Property(
        type = PropertyType.COLOR,
        name = "Border Color",
        description = "What color should borders be?",
        category = "General",
        subcategory = "Misc - Colors",
    )
    var borderColor = Color.YELLOW

    @Property(
        type = PropertyType.COLOR,
        name = "Fill Color",
        description = "What color should fill-colors (of boxes) be?",
        category = "General",
        subcategory = "Misc - Colors",
    )
    var fillColor = Color(0, 0, 0, 100)




    companion object {

        val rarityOrder = Rarity.values()

        fun compare(o1: PropertyData, o2: PropertyData): Int {
            val o1Name = o1.attributesExt.name
            val o2Name = o2.attributesExt.name


            val sortingOrder = o1.attributesExt.maxF
            val sortingOrder2 = o2.attributesExt.maxF

            if(sortingOrder == 0f && sortingOrder2 == 0f) {
                return o1Name.compareTo(o2Name)
            }

            if(sortingOrder == 0f) {
                return 1
            }

            if(sortingOrder2 == 0f) {
                return -1
            }

            return sortingOrder.compareTo(sortingOrder2)
        }
    }

    class CustomSortingBehavior : SortingBehavior() {
        override fun getPropertyComparator(): Comparator<in PropertyData> {
            // if the property name is "Enabled", put it at the top
            return Comparator { o1, o2 ->
                if (o1.attributesExt.name == "Toggle") {
                    -1
                } else if (o2.attributesExt.name == "Toggle") {
                    1
                } else {
                    compare(o1, o2)
                }
            }
        }

        override fun getCategoryComparator(): Comparator<in Category> {
            // if category is general, put it at the top
            return Comparator { o1, o2 ->
                if (o1.name == "General") {
                    -1
                } else if (o2.name == "General") {
                    1
                } else {
                    o1.name.compareTo(o2.name)
                }
            }
        }

        override fun getSubcategoryComparator(): Comparator<in Map.Entry<String, List<PropertyData>>> {
            // if subcategory is general, put it at the top
            return Comparator { o1, o2 ->
                if (o1.key == "General") {
                    -1
                } else if (o2.key == "General") {
                    1
                } else {
                    o1.key.compareTo(o2.key)
                }
            }
        }

    }
}