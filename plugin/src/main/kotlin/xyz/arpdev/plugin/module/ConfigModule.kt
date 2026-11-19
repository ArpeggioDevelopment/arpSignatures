package xyz.arpdev.plugin.module

import xyz.arpdev.plugin.config.GeneralConfig
import xyz.arpdev.plugin.config.GeneralConfigLoader
import xyz.arpdev.plugin.config.MessagesConfig
import xyz.arpdev.plugin.multification.PaperMultification
import com.eternalcode.multification.okaeri.MultificationSerdesPack
import com.google.inject.AbstractModule
import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class ConfigModule(
    private val plugin: JavaPlugin
) : AbstractModule() {
    private val multification = PaperMultification(MessagesConfig())

    override fun configure() {
        val messagesConfig = ConfigManager.create(MessagesConfig::class.java) { init ->
            init.configure { conf ->
                conf.configurer(YamlBukkitConfigurer(), MultificationSerdesPack(multification))
                conf.bindFile(File(plugin.dataFolder, "messages.yml"))
                conf.removeOrphans(true)
            }
            init.saveDefaults()
            init.load(true)
        }
        val generalConfigLoader = GeneralConfigLoader()
        val generalConfig = generalConfigLoader.load(File(plugin.dataFolder, "general.yml"))
        generalConfig.save()

        bind(MessagesConfig::class.java).toInstance(messagesConfig)
        bind(GeneralConfig::class.java).toInstance(generalConfig)
        bind(GeneralConfigLoader::class.java).toInstance(generalConfigLoader)

        bind(PaperMultification::class.java)
    }
}
