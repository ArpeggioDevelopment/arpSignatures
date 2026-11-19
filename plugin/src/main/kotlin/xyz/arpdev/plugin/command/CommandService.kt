package xyz.arpdev.plugin.command

import xyz.arpdev.plugin.command.commands.ReloadCommand
import xyz.arpdev.plugin.command.commands.SignCommand
import xyz.arpdev.plugin.command.handler.DefaultInvalidUsageHandler
import xyz.arpdev.plugin.command.handler.DefaultMissingPermissionHandler
import com.google.inject.Inject
import dev.rollczi.litecommands.LiteCommands
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory
import dev.rollczi.litecommands.folia.FoliaExtension
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class CommandService @Inject constructor(
    private val plugin: JavaPlugin,
    private val signCommand: SignCommand,
    private val reloadCommand: ReloadCommand,
    private val invalidUsageHandler: DefaultInvalidUsageHandler,
    private val missingPermissionHandler: DefaultMissingPermissionHandler
) {
    private lateinit var liteCommands: LiteCommands<CommandSender>

    fun registerCommands() {
        liteCommands = LiteBukkitFactory.builder(plugin)
            .commands(
                signCommand,
                reloadCommand
            )
            .extension(FoliaExtension(plugin))
            .invalidUsage(invalidUsageHandler)
            .missingPermission(missingPermissionHandler)
            .build()
    }

    fun unregisterCommands() {
        if (::liteCommands.isInitialized) {
            liteCommands.unregister()
        }
    }
}
