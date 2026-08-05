package xyz.arpdev.plugin.command.handler

import xyz.arpdev.plugin.config.MessagesConfig
import xyz.arpdev.plugin.multification.PaperMultification
import com.google.inject.Inject
import dev.rollczi.litecommands.handler.result.ResultHandlerChain
import dev.rollczi.litecommands.invalidusage.InvalidUsage
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler
import dev.rollczi.litecommands.invocation.Invocation
import org.bukkit.command.CommandSender

class DefaultInvalidUsageHandler @Inject constructor(
    private val messagesConfig: MessagesConfig,
    private val multification: PaperMultification
) : InvalidUsageHandler<CommandSender> {

    override fun handle(
        invocation: Invocation<CommandSender>,
        result: InvalidUsage<CommandSender>,
        chain: ResultHandlerChain<CommandSender>
    ) {
        val sender = invocation.sender()
        val schematic = result.schematic

        multification.notify(
            sender,
            messagesConfig.messages.invalidUsage,
            "{USAGE}" to schematic.first()
        )
    }
}
