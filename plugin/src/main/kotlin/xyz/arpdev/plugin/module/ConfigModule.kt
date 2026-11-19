package xyz.arpdev.plugin.module

import xyz.arpdev.plugin.config.ConfigService
import xyz.arpdev.plugin.config.GeneralConfigLoader
import xyz.arpdev.plugin.config.MessagesConfigLoader
import xyz.arpdev.plugin.multification.PaperMultification
import com.google.inject.AbstractModule
import org.bukkit.plugin.java.JavaPlugin

class ConfigModule(
    private val plugin: JavaPlugin
) : AbstractModule() {
    override fun configure() {
        val configService = ConfigService(
            plugin.dataFolder,
            GeneralConfigLoader(),
            MessagesConfigLoader()
        )

        bind(ConfigService::class.java).toInstance(configService)

        bind(PaperMultification::class.java)
    }
}
