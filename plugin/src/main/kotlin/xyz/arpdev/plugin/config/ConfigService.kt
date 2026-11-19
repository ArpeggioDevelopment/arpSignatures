package xyz.arpdev.plugin.config

import java.io.File

class ConfigService(
    private val dataFolder: File,
    private val generalConfigLoader: GeneralConfigLoader,
    private val messagesConfigLoader: MessagesConfigLoader
) {

    @Volatile
    var current: ConfigSnapshot = load()
        private set

    init {
        current.general.save()
        current.messages.save()
    }

    @Synchronized
    fun reload(): ConfigSnapshot {
        val loaded = load()
        current = loaded
        return loaded
    }

    private fun load(): ConfigSnapshot {
        val general = generalConfigLoader.load(File(dataFolder, "general.yml"))
        val messages = messagesConfigLoader.load(File(dataFolder, "messages.yml"))

        return ConfigSnapshot(general, messages)
    }
}
