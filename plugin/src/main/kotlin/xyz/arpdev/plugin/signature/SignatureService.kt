package xyz.arpdev.plugin.signature

import xyz.arpdev.plugin.config.GeneralConfig
import xyz.arpdev.plugin.config.MessagesConfig
import xyz.arpdev.plugin.multification.PaperMultification
import xyz.arpdev.plugin.signature.format.SignatureFormatter
import xyz.arpdev.plugin.signature.item.SignatureApplier
import xyz.arpdev.plugin.signature.model.FailureReason
import xyz.arpdev.plugin.signature.model.Signature
import xyz.arpdev.plugin.signature.model.ValidationResult
import xyz.arpdev.plugin.signature.validation.SignatureValidator
import com.google.inject.Inject
import com.google.inject.Singleton
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Singleton
class SignatureService @Inject constructor(
    private val generalConfig: GeneralConfig,
    private val messagesConfig: MessagesConfig,
    private val validator: SignatureValidator,
    private val formatter: SignatureFormatter,
    private val applier: SignatureApplier,
    private val multification: PaperMultification
) {

    fun validate(
        player: Player,
        item: ItemStack,
        signature: String
    ): Boolean {
        when (val result = validator.validate(item, signature)) {
            ValidationResult.Success -> return true

            is ValidationResult.Failure -> {
                when (result.reason) {
                    FailureReason.INVALID_LENGTH -> {
                        val restrictions = generalConfig.restrictions

                        multification.notify(
                            player,
                            messagesConfig.messages.notInRange,
                            "{MIN}" to restrictions.minCharacters.toString(),
                            "{MAX}" to restrictions.maxCharacters.toString(),
                            "{SIGNATURE}" to MiniMessage.miniMessage().escapeTags(signature)
                        )
                    }

                    FailureReason.NO_ITEM -> multification.notify(player, messagesConfig.messages.noItem)
                    FailureReason.BLACKLISTED_ITEM -> multification.notify(player, messagesConfig.messages.blacklistedItem)
                    FailureReason.INVALID_SIGNATURE -> multification.notify(player, messagesConfig.messages.invalidSignature)
                }

                return false
            }
        }
    }

    fun sign(
        player: Player,
        item: ItemStack,
        text: String
    ) {
        val signature = Signature(
            text = text,
            author = player.name,
            createdAt = LocalDateTime.now()
        )
        val component = formatter.format(player, signature)
        applier.apply(item, component)

        val safeText = MiniMessage.miniMessage().escapeTags(signature.text)

        multification.notify(
            player,
            messagesConfig.messages.signed,
            "{DATE}" to signature.createdAt.format(DateTimeFormatter.ofPattern(generalConfig.dateFormat)),
            "{SIGNATURE}" to safeText
        )
    }
}
