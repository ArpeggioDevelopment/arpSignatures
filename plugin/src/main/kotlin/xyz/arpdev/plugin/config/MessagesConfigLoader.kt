package xyz.arpdev.plugin.config

import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults
import com.eternalcode.multification.okaeri.MultificationSerdesPack
import com.google.inject.Singleton
import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
import java.io.File

@Singleton
class MessagesConfigLoader {

    private val noticeRegistry = NoticeResolverDefaults.createRegistry()

    fun load(file: File): MessagesConfig {
        return ConfigManager.create(MessagesConfig::class.java) { init ->
            init.configure { conf ->
                conf.configurer(
                    YamlBukkitConfigurer(),
                    MultificationSerdesPack(noticeRegistry)
                )
                conf.bindFile(file)
                conf.removeOrphans(true)
            }
            init.saveDefaults()
            init.load()
        }
    }
}
