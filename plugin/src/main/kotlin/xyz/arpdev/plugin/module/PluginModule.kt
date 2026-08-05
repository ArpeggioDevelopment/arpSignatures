package xyz.arpdev.plugin.module

import com.google.inject.AbstractModule
import com.google.inject.Inject
import org.bukkit.plugin.java.JavaPlugin

class PluginModule @Inject constructor(
    val plugin: JavaPlugin
) : AbstractModule() {

    override fun configure() {
        bind(JavaPlugin::class.java).toInstance(plugin)

    }
}
