package xyz.arpdev.plugin.command.commands

import xyz.arpdev.plugin.config.GeneralConfig
import xyz.arpdev.plugin.config.MessagesConfig
import xyz.arpdev.plugin.multification.PaperMultification
import com.google.inject.Inject
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import kotlin.system.measureTimeMillis

@Command(name = "arpsignatures")
@Permission("arpsignatures.reload")
class ReloadCommand @Inject constructor(
    private val plugin: JavaPlugin,
    private val generalConfig: GeneralConfig,
    private val messagesConfig: MessagesConfig,
    private val paperMultification: PaperMultification
) {

    @Execute(name = "reload")
    fun reload(
        @Context sender: CommandSender
    ) {
        paperMultification.notify(sender, messagesConfig.messages.reloading)
        try {
            val time = measureTimeMillis {
                generalConfig.load()
                messagesConfig.load()
            }

            paperMultification.notify(sender, messagesConfig.messages.reloaded, "{TIME}" to time.toString())
        } catch (e: Exception) {
            paperMultification.notify(sender, messagesConfig.messages.reloadError)
            plugin.logger.severe(e.message)
        }
    }
}
