package de.bybackfish.telosaddons.core.commands

import net.minecraft.text.Text

open class Command(open val name: String, private val description: String = "", private val usage: String = "/$name") :
    CommandUtil() {
    open fun buildHelp(parent: String = ""): List<Text> {
        return listOf(
            Text.of(" -${parent} $name - $description - $usage")
        )
    }

}
