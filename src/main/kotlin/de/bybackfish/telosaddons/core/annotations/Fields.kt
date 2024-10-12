package de.bybackfish.telosaddons.core.annotations

import gg.essential.vigilance.data.PropertyType

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Keybind(
    val defaultKey: Int = 0,
    val inGUI: Boolean = false
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class OverlayInfo(

)


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Button(
    val sortingOrder: Int = 0,
    val description: String = "",
    val searchTags: Array<String> = [],
    val hidden: Boolean = false,
    val buttonText: String = ""
)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Property(
    val sortingOrder: Int = 0,
    val forceType: PropertyType = PropertyType.CUSTOM,
    val description: String = "",
    val searchTags: Array<String> = [],
    val min: Int = 0,
    val max: Int = 0,
    val decimalPlaces: Int = 1,
    val increment: Int = 1,
    val options: Array<String> = [],
    val allowAlpha: Boolean = true,
    val placeholder: String = "",
    val triggerActionOnInitialization: Boolean = true,
    val hidden: Boolean = false,
)