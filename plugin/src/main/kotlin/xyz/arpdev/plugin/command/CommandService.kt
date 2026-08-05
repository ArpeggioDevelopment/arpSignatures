package xyz.arpdev.plugin.command

import xyz.arpdev.plugin.command.commands.ReloadCommand
import xyz.arpdev.plugin.command.commands.SignCommand
import xyz.arpdev.plugin.command.handler.DefaultInvalidUsageHandler
import xyz.arpdev.plugin.command.handler.DefaultMissingPermissionHandler
import com.google.inject.Inject
import com.google.inject.Injector
import dev.rollczi.litecommands.LiteCommands
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class CommandService @Inject constructor(
    private val plugin: JavaPlugin,
    private val injector: Injector
) {
    private lateinit var liteCommands: LiteCommands<CommandSender>

    fun registerCommands() {
        liteCommands = LiteBukkitFactory.builder(plugin)
            .commands(
                injector.getInstance(SignCommand::class.java),
                injector.getInstance(ReloadCommand::class.java)
            )
            .invalidUsage(injector.getInstance(DefaultInvalidUsageHandler::class.java))
            .missingPermission(injector.getInstance(DefaultMissingPermissionHandler::class.java))
            .build()
    }

    fun unregisterCommands() {
        if (::liteCommands.isInitialized) {
            liteCommands.unregister()
        }
    }
}
