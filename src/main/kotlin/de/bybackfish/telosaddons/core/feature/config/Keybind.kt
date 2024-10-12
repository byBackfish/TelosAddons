package de.bybackfish.telosaddons.core.feature.config

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Keybind(
    val name: String
)