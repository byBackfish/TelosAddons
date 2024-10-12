package de.bybackfish.telosaddons.core.feature

import de.bybackfish.telosaddons.TelosAddons
import de.bybackfish.telosaddons.TelosConfig
import de.bybackfish.telosaddons.core.annotations.EnabledByDefault
import de.bybackfish.telosaddons.core.feature.struct.FeatureState
import de.bybackfish.telosaddons.core.getKey
import de.bybackfish.telosaddons.core.getTranslatedName
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.CallablePropertyValue
import gg.essential.vigilance.data.PropertyData
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.PropertyValue
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity

abstract class Feature  {
    var state = FeatureState.UNINITIALIZED

    lateinit var featureInfo: FeatureManager.FeatureInfo
    private var _nativeSettings: List<LocalProperty> = listOf()

    private var customProperties = mutableMapOf<String, Any>()

    val mc: MinecraftClient
        get() = MinecraftClient.getInstance()

    val player: ClientPlayerEntity?
        get() = mc.player

    fun init() {
        if (state != FeatureState.UNINITIALIZED) return

        state = if (this::class.annotations.any { annotation -> annotation is EnabledByDefault })
            FeatureState.ENABLED
        else
            FeatureState.DISABLED

        val condition = { state == FeatureState.ENABLED }
        TelosAddons.bus.register(this, condition)
    }

    open fun postInit() {}

    fun toggle() {
        state = if (state == FeatureState.ENABLED) FeatureState.DISABLED else FeatureState.ENABLED
    }

    fun timeExpired(time: Long, duration: Long): Boolean {
        println("time: $time, duration: $duration, now: ${System.currentTimeMillis()}, result: ${System.currentTimeMillis() > time + duration}, expires at: ${time + duration}")
        return System.currentTimeMillis() > time + duration
    }

    fun markDirty() {
        TelosAddons.config.markDirty()
    }

    fun loadLocalProperties(config: TelosConfig, featureManager: FeatureManager, featureToggle: PropertyData? = null) {
        for(property in _nativeSettings) {
            val value = property.value ?: LocalFeatureProperty(this, property.name)
            val data = featureManager.nativeRegisterProperty<Any>(
                value = value,
                type = property.type,
                name = getTranslatedName(getKey(this::class) + "." + property.name),
                description = property.description,
                searchTags = property.searchTags.toList(),

                category = this.featureInfo.category,
                subcategory = getTranslatedName(getKey(this::class)),

                min = property.min,
                max = property.max,
                sortingOrder = property.sortingOrder,
                decimalPlaces = property.decimalPlaces,
                increment = property.increment,
                allowAlpha = property.allowAlpha,
                options = property.options.toList(),
                placeholder = property.placeholder,
                triggerActionOnInitialization = property.triggerActionOnInitialization,
                hidden = property.hidden,
                config = config,
            )

            if (featureToggle != null) {
                data.dependsOn = featureToggle
                featureToggle.hasDependants = true
            }
        }
    }

    fun addSetting(data: LocalProperty) {
        customProperties[data.name] = data.default
        _nativeSettings += data
    }

    fun <T> property(name: String): T? {
        return customProperties[name] as T?
    }

    fun <T> property(name: String, value: T) {
        customProperties[name] = value as Any
        TelosAddons.config.markDirty()
    }


    class LocalFeatureProperty(val feature: Feature, private val key: String) : PropertyValue() {
        override fun getValue(instance: Vigilant): Any? {
            return feature.customProperties[key]
        }

        override fun setValue(value: Any?, instance: Vigilant) {
            feature.customProperties[key] = value as Any
        }
    }

    class LocalCallableProperty(val callback: () -> Any): CallablePropertyValue() {
        override fun invoke(instance: Vigilant) {
            callback()
        }
    }

    data class LocalProperty(
        val name: String,
        val type: PropertyType,
        val default: Any,

        val value: PropertyValue? = null,
        val sortingOrder: Int = 0,
        val description: String = "",
        val searchTags: Array<String> = arrayOf(),
        val min: Int = 0,
        val max: Int = 0,
        val decimalPlaces: Int = 1,
        val increment: Int = 1,
        val options: Array<String> = arrayOf(),
        val allowAlpha: Boolean = true,
        val placeholder: String = "",
        val triggerActionOnInitialization: Boolean = true,
        val hidden: Boolean = false,
    )

}