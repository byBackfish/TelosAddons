package de.bybackfish.telosaddons.core.feature

import de.bybackfish.telosaddons.TelosAddons
import de.bybackfish.telosaddons.TelosConfig
import de.bybackfish.telosaddons.core.annotations.*
import de.bybackfish.telosaddons.core.annotations.Category
import de.bybackfish.telosaddons.core.annotations.Property
import de.bybackfish.telosaddons.core.event.Subscribe
import de.bybackfish.telosaddons.core.feature.overlay.Overlay
import de.bybackfish.telosaddons.core.feature.overlay.OverlayPosition
import de.bybackfish.telosaddons.core.feature.struct.FeatureState
import de.bybackfish.telosaddons.core.getKey
import de.bybackfish.telosaddons.core.getTranslatedName
import de.bybackfish.telosaddons.events.GUIKeyPressEvent
import de.bybackfish.telosaddons.events.RenderScreenEvent
import de.bybackfish.telosaddons.screen.OverlayMoveScreen
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.*
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.*


class FeatureManager {

    val features = mutableMapOf<KClass<out Feature>, Feature>()

    private val lastKeybindTime = mutableMapOf<KFunction<*>, Long>()
    private val KEYBIND_TIMEOUT = 200L


    init {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { _: MinecraftClient ->
            features.forEach { (_, feature) ->
                if (feature.state != FeatureState.ENABLED) return@forEach
                feature.featureInfo.keybindings.forEach forKeybind@{ keybindSetting ->

                    if (keybindSetting.keybinding.wasPressed() && !keybindSetting.annotation.inGUI) {
                        keybindSetting.function.call(feature)
                    }
                }
            }
        })
    }

    @Subscribe
    fun onGuiKey(event: GUIKeyPressEvent) {
        features.forEach { (_, feature) ->
            if (feature.state != FeatureState.ENABLED) return@forEach
            feature.featureInfo.keybindings.forEach { keybindSetting ->

                if (keybindSetting.keybinding.matchesKey(
                        event.key,
                        event.scanCode
                    ) && keybindSetting.annotation.inGUI
                ) {
                    if (keybindSetting.function.parameters.size == 2) {
                        keybindSetting.function.call(feature, event)
                    } else {
                        keybindSetting.function.call(feature)
                    }
                }
            }
        }
    }

    @Subscribe
    fun onRender(event: RenderScreenEvent) {
        val isMoving = MinecraftClient.getInstance().currentScreen is OverlayMoveScreen
        if (isMoving) return
        features.forEach { (_, feature) ->
            if (feature.state != FeatureState.ENABLED) return@forEach
            feature.featureInfo.overlays.forEach { overlaySetting ->
                if(overlaySetting.overlay.shouldRender())
                    overlaySetting.overlay.render(event.context, event.delta.getTickDelta(true))
            }
        }
    }

    fun register(vararg features: Feature) {
        features.forEach { register(it) }
    }

    fun register(feature: Feature) {
        val propertySettings = getProperties(feature::class).map {
            PropertySetting(
                feature,
                it,
                it.annotations.first { annotation -> annotation is Property } as Property)
        }

        val buttons = getButtons(feature::class).map {
            val annotation =
                it.annotations.first { annotation -> annotation is Button } as Button
            ButtonSetting(feature, it, annotation)
        }


        val shouldRegisterKeybindings =
            feature::class.annotations.any { annotation -> annotation is RegisterKeybinds }
        val keybindSettings = if (shouldRegisterKeybindings) {
            getKeybindings(feature::class).map {
                val annotation =
                    it.annotations.first { annotation -> annotation is Keybind } as Keybind
                val mcKeybind = KeyBinding(
                    getTranslatedName(getKey(feature::class, it)),
                    annotation.defaultKey,
                    "TelosAddons - ${getTranslatedName(getKey(feature::class))}"
                )
                KeyBindingHelper.registerKeyBinding(mcKeybind)

                KeybindSetting(feature, it, annotation, mcKeybind)
            }
        } else listOf()

        val overlaySettings = getOverlays(feature::class).map {
            val newInstance = it.primaryConstructor!!.call(feature) as Overlay
            OverlaySetting(
                feature,
                it,
                it.annotations.first { annotation -> annotation is OverlayInfo } as OverlayInfo,
                newInstance,
                true
            )
        }

        val featureCategory =
            feature::class.annotations.firstOrNull { annotation -> annotation is Category }
                ?.let { (it as Category).name } ?: "default"
        val featureInfo = FeatureInfo(
            feature,
            featureCategory,
            propertySettings,
            keybindSettings,
            overlaySettings,
            buttons
        )

        feature.featureInfo = featureInfo

        feature.init()
        features[feature::class] = feature
    }

    fun loadToConfig() {
        println("[Telos] Loading Settings to Config")
        val config = TelosAddons.config
        features.forEach { (clazz, feature) ->
            println("[Telos] Loading Feature ${clazz.simpleName} with properties: ${feature.featureInfo.properties.size}")
            val featureData = nativeRegisterProperty<Nothing>(
                value = FeatureTogglePropertyValue(feature),
                type = PropertyType.SWITCH,
                name = "Toggle",
                description = "Enable ${getTranslatedName(getKey(clazz))}",
                category = feature.featureInfo.category,
                subcategory = getTranslatedName(getKey(clazz)),
                config = config,
            )

            feature.featureInfo.properties.forEach { propertySetting ->
                nativeRegisterFeaturePropertySetting(
                    config,
                    feature,
                    propertySetting,
                    featureData
                )
            }

            feature.featureInfo.buttons.forEach { buttonSetting ->
                nativeRegisterFeatureButton(
                    config,
                    feature,
                    buttonSetting,
                    featureData
                )
            }

            feature.loadLocalProperties(config, this, featureData)
        }

    }

    fun <T> nativeRegisterProperty(
        config: TelosConfig,
        value: PropertyValue,
        type: PropertyType,
        name: String,
        description: String = "",
        subcategory: String = "",
        category: String = "",
        searchTags: List<String> = listOf(),
        min: Int = 0,
        max: Int = 0,
        sortingOrder: Int = 0,
        decimalPlaces: Int = 1,
        increment: Int = 1,
        options: List<String> = listOf(),
        allowAlpha: Boolean = true,
        placeholder: String = "",
        protectedText: Boolean = false,
        triggerActionOnInitialization: Boolean = true,
        hidden: Boolean = false,
        action: ((T) -> Unit)? = null,
        customPropertyInfo: KClass<out PropertyInfo> = Nothing::class,
    ): PropertyData {

        val data = PropertyData(
            PropertyAttributesExt(
                type = type,
                name = name,
                category = category,
                subcategory = subcategory,
                description = description,
                min = min,
                max = max,
                decimalPlaces = decimalPlaces,
                maxF = sortingOrder.toFloat(),
                increment = increment,
                options = options,
                allowAlpha = allowAlpha,
                placeholder = placeholder,
                protected = protectedText,
                triggerActionOnInitialization = triggerActionOnInitialization,
                hidden = hidden,
                searchTags = searchTags,
                customPropertyInfo = customPropertyInfo.java,
            ),
            value,
            config
        )

        println("[Telos] Registering Property $name")


        TelosAddons.propertyCollector.addProperty(data)

        return data;
    }

    private fun nativeRegisterFeaturePropertySetting(
        config: TelosConfig,
        feature: Feature,
        propertySetting: PropertySetting,
        featureToggle: PropertyData? = null
    ) {
        val data = nativeRegisterProperty<Any>(
            value = CustomValueProperty(
                feature,
                propertySetting.field as KMutableProperty1<Feature, Any>
            ),
            type = propertySetting.getPropertyType(),
            name = getTranslatedName(getKey(feature::class, propertySetting.field)),
            description = propertySetting.property.description,
            searchTags = propertySetting.property.searchTags.toList(),

            category = propertySetting.feature.featureInfo.category,
            subcategory = getTranslatedName(getKey(feature::class)),

            min = propertySetting.property.min,
            max = propertySetting.property.max,
            sortingOrder = propertySetting.property.sortingOrder,
            decimalPlaces = propertySetting.property.decimalPlaces,
            increment = propertySetting.property.increment,
            allowAlpha = propertySetting.property.allowAlpha,
            options = propertySetting.property.options.toList(),
            placeholder = propertySetting.property.placeholder,
            triggerActionOnInitialization = propertySetting.property.triggerActionOnInitialization,
            hidden = propertySetting.property.hidden,
            config = config,
        )

        if (featureToggle != null) {
            data.dependsOn = featureToggle
            featureToggle.hasDependants = true
        }
    }

    private fun nativeRegisterFeatureButton(
        config: TelosConfig,
        feature: Feature,
        buttonSetting: ButtonSetting,
        featureToggle: PropertyData? = null
    ) {
        val data = nativeRegisterProperty<Any>(
            value = CustomButtonProperty(
                feature,
                buttonSetting.function
            ),
            type = PropertyType.BUTTON,
            name = getTranslatedName(getKey(buttonSetting.feature::class, buttonSetting.function)),
            description = buttonSetting.annotation.description,
            searchTags = buttonSetting.annotation.searchTags.toList(),
            sortingOrder = buttonSetting.annotation.sortingOrder,
            category = buttonSetting.feature.featureInfo.category,
            subcategory = getTranslatedName(getKey(feature::class)),

            placeholder = buttonSetting.annotation.buttonText,
            hidden = buttonSetting.annotation.hidden,
            action = null,
            config = config,
        )

        if (featureToggle != null) {
            data.dependsOn = featureToggle
            featureToggle.hasDependants = true
        }
    }


    inline fun <reified T : Feature> getFeature(clazz: KClass<out T>): T? {
        return features[clazz] as? T
    }
    // make it so i can use getFeature<ItemViewer>()
    inline fun <reified T : Feature> getFeature(): T? {
        return features[T::class] as? T
    }

    private fun getProperties(clazz: KClass<*>): List<KMutableProperty1<*, *>> {
        println("[Telos] Raw Members: ${clazz.declaredMemberProperties.size}")

        clazz.declaredMemberProperties.forEach {
            println("[Telos] Member: ${it.name}: ${it.annotations}")
        }

        return clazz.declaredMemberProperties.filter { it.annotations.any { annotation -> annotation is Property } }
            .also {
                println("[Telos] Found Property: ${it.size}")
            }
            .map { it as KMutableProperty1<*, *> }
    }


    private fun getButtons(clazz: KClass<*>): List<KFunction<*>> {
        return clazz.memberFunctions.filter { it.annotations.any { annotation -> annotation is Button } }
    }

    private fun getOverlays(clazz: KClass<*>): List<KClass<*>> {
        return clazz.nestedClasses.filter { it.annotations.any { annotation -> annotation is OverlayInfo } }
    }

    private fun getKeybindings(clazz: KClass<*>): List<KFunction<*>> {
        return clazz.memberFunctions.filter { it.annotations.any { annotation -> annotation is Keybind } }
    }

    data class FeatureInfo(
        val feature: Feature,
        val category: String,
        val properties: List<PropertySetting>,
        val keybindings: List<KeybindSetting>,
        val overlays: List<OverlaySetting>,
        val buttons: List<ButtonSetting>,
    )

    data class PropertySetting(
        val feature: Feature,
        val field: KMutableProperty1<*, *>,
        val property: Property,
    ) {
        fun getPropertyType(): PropertyType {
            if (this.property.forceType != PropertyType.CUSTOM)
                return this.property.forceType

            // check if field is int or boolean
            return when (field.returnType) {
                Int::class.createType() -> {
                    PropertyType.NUMBER
                }

                Boolean::class.createType() -> {
                    PropertyType.SWITCH
                }

                String::class.createType() -> {
                    PropertyType.TEXT
                }

                else -> {
                    PropertyType.TEXT
                }

            }
        }
    }

    data class KeybindSetting(
        val feature: Feature,
        val function: KFunction<*>,
        val annotation: Keybind,
        val keybinding: KeyBinding,
    )

    data class OverlaySetting(
        val feature: Feature,
        val clazz: KClass<*>,
        val overlayInfo: OverlayInfo,
        val overlay: Overlay,
        var enabled: Boolean
    )

    data class ButtonSetting(
        val feature: Feature,
        val function: KFunction<*>,
        val annotation: Button,
    )

    class CustomValueProperty<F, T>(val owner: F, internal val property: KMutableProperty1<F, T>) :
        PropertyValue() {
        override fun getValue(instance: Vigilant): Any? {
            return property.get(owner)
        }

        override fun setValue(value: Any?, instance: Vigilant) {
            try {
                property.set(owner, value as T)
            } catch (e: Exception) {
                println("[Telos] Error setting property: ${property.name} to value: $value")
                e.printStackTrace()
            }
        }
    }

    class CustomButtonProperty(private val owner: Feature, internal val function: KFunction<*>) :
        CallablePropertyValue() {
        override fun invoke(instance: Vigilant) {
            function.call(owner)
        }
    }

    class FeatureTogglePropertyValue(private val feature: Feature) : PropertyValue() {
        override fun getValue(instance: Vigilant): Any? {
            return feature.state == FeatureState.ENABLED
        }

        override fun setValue(value: Any?, instance: Vigilant) {
            if (value as Boolean) {
                if (feature.state == FeatureState.ENABLED) return
                feature.toggle()
            } else {
                if (feature.state == FeatureState.DISABLED) return
                feature.toggle()
            }
        }
    }

    data class OverlayConfigFile(
        var overlays: Map<String, OverlayPosition>
    )
}

