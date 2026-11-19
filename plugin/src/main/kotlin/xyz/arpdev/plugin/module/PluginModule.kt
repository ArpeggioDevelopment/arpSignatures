package xyz.arpdev.plugin.module

import com.google.inject.AbstractModule
import org.bukkit.plugin.java.JavaPlugin

class PluginModule(
    private val plugin: JavaPlugin
) : AbstractModule() {

    override fun configure() {
        bind(JavaPlugin::class.java).toInstance(plugin)
    }
}
