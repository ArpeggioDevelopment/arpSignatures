package xyz.arpdev.plugin

import xyz.arpdev.plugin.command.CommandService
import xyz.arpdev.plugin.module.ConfigModule
import xyz.arpdev.plugin.module.PluginModule
import com.google.inject.Guice
import org.bukkit.plugin.java.JavaPlugin

class SignaturesPlugin : JavaPlugin() {
    private lateinit var commandService: CommandService

    override fun onEnable() {
        val injector = Guice.createInjector(
            PluginModule(this),
            ConfigModule(this)
        )
        commandService = injector.getInstance(CommandService::class.java)
        commandService.registerCommands()
    }

    override fun onDisable() {
        if (::commandService.isInitialized) {
            commandService.unregisterCommands()
        }
    }
}
