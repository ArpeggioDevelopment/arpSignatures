package xyz.arpdev.plugin.command.commands

import xyz.arpdev.plugin.config.ConfigService
import xyz.arpdev.plugin.multification.PaperMultification
import com.google.inject.Inject
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level
import kotlin.time.measureTimedValue

@Command(name = "arpsignatures")
@Permission("arpsignatures.reload")
class ReloadCommand @Inject constructor(
    private val plugin: JavaPlugin,
    private val configService: ConfigService,
    private val paperMultification: PaperMultification
) {

    @Execute(name = "reload")
    fun reload(
        @Context sender: CommandSender
    ) {
        val previous = configService.current
        paperMultification.notify(sender, previous.messages.messages.reloading)

        val (loaded, time) = try {
            measureTimedValue {
                configService.reload()
            }
        } catch (e: Exception) {
            plugin.logger.log(Level.SEVERE, "Failed to reload configuration. Previous settings remain active.", e)
            paperMultification.notify(sender, previous.messages.messages.reloadError)
            return
        }

        paperMultification.notify(sender, loaded.messages.messages.reloaded, "{TIME}" to time.inWholeMilliseconds.toString())
    }
}
