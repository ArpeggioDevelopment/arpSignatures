package xyz.arpdev.plugin

import xyz.arpdev.plugin.command.CommandService
import xyz.arpdev.plugin.module.ConfigModule
import xyz.arpdev.plugin.module.PluginModule
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import org.bukkit.plugin.java.JavaPlugin

class SignaturesPlugin : JavaPlugin() {
    private lateinit var injector: Injector
    @Inject lateinit var commandService: CommandService

    override fun onEnable() {
        injector = Guice.createInjector(
            PluginModule(this),
            ConfigModule(this)
        )
        injector.injectMembers(this)

        commandService.registerCommands()
    }

    override fun onDisable() {
        if (::commandService.isInitialized) {
            commandService.unregisterCommands()
        }
    }
}
