package xyz.arpdev.plugin.command.commands

import xyz.arpdev.plugin.signature.SignatureService
import com.google.inject.Inject
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.join.Join
import dev.rollczi.litecommands.annotations.permission.Permission
import org.bukkit.entity.Player

@Command(name = "sign")
@Permission("arpsignatures.use")
class SignCommand @Inject constructor(
    private val signatureService: SignatureService
) {

    @Execute
    fun sign(
        @Context sender: Player,
        @Join("text") text: String
    ) {
        val item = sender.inventory.itemInMainHand

        if (!signatureService.validate(sender, item, text)) {
            return
        }

        signatureService.sign(sender, item, text)
    }
}
