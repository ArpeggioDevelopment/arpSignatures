package xyz.arpdev.plugin.signature

import xyz.arpdev.plugin.config.ConfigService
import xyz.arpdev.plugin.config.ConfigSnapshot
import xyz.arpdev.plugin.multification.PaperMultification
import xyz.arpdev.plugin.signature.format.SignatureFormatter
import xyz.arpdev.plugin.signature.item.SignatureApplier
import xyz.arpdev.plugin.signature.model.FailureReason
import xyz.arpdev.plugin.signature.model.Signature
import xyz.arpdev.plugin.signature.model.ValidationResult
import xyz.arpdev.plugin.signature.validation.SignatureValidator
import com.google.inject.Inject
import com.google.inject.Singleton
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.LocalDateTime

@Singleton
class SignatureService @Inject constructor(
    private val configService: ConfigService,
    private val validator: SignatureValidator,
    private val formatter: SignatureFormatter,
    private val applier: SignatureApplier,
    private val multification: PaperMultification
) {

    private fun validate(
        player: Player,
        item: ItemStack,
        signature: String,
        config: ConfigSnapshot
    ): Boolean {
        val messages = config.messages.messages

        when (val result = validator.validate(item, signature, config.general)) {
            ValidationResult.Success -> return true

            is ValidationResult.Failure -> {
                when (result.reason) {
                    FailureReason.INVALID_LENGTH -> {
                        val restrictions = config.general.restrictions

                        multification.notify(
                            player,
                            messages.notInRange,
                            "{MIN}" to restrictions.minCharacters.toString(),
                            "{MAX}" to restrictions.maxCharacters.toString(),
                            "{SIGNATURE}" to signature
                        )
                    }

                    FailureReason.NO_ITEM -> multification.notify(player, messages.noItem)
                    FailureReason.BLACKLISTED_ITEM -> multification.notify(player, messages.blacklistedItem)
                    FailureReason.INVALID_SIGNATURE -> multification.notify(player, messages.invalidSignature)
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
        val config = configService.current
        if (!validate(player, item, text, config)) {
            return
        }

        val signature = Signature(
            text = text,
            author = player.name,
            createdAt = LocalDateTime.now()
        )
        val component = formatter.format(player, signature, config.general)
        applier.apply(item, component)

        multification.notify(
            player,
            config.messages.messages.signed,
            "{DATE}" to signature.createdAt.format(config.general.dateFormatter),
            "{SIGNATURE}" to signature.text
        )
    }
}
