package xyz.arpdev.plugin.command.handler

import xyz.arpdev.plugin.config.MessagesConfig
import xyz.arpdev.plugin.multification.PaperMultification
import com.google.inject.Inject
import dev.rollczi.litecommands.handler.result.ResultHandlerChain
import dev.rollczi.litecommands.invocation.Invocation
import dev.rollczi.litecommands.permission.MissingPermissions
import dev.rollczi.litecommands.permission.MissingPermissionsHandler
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DefaultMissingPermissionHandler @Inject constructor(
    private val messagesConfig: MessagesConfig,
    private val multification: PaperMultification
) : MissingPermissionsHandler<CommandSender> {

    override fun handle(
        invocation: Invocation<CommandSender?>?,
        missingPermissions: MissingPermissions?,
        chain: ResultHandlerChain<CommandSender?>?
    ) {
        val sender = invocation?.sender()
        if (sender !is Player) return

        multification.notify(
            sender,
            messagesConfig.messages.noPermission,
            "{PERMISSIONS}" to (missingPermissions?.asJoinedText(", ") ?: "")
        )
    }
}
