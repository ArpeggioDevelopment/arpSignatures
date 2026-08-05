package xyz.arpdev.plugin.signature.validation

import xyz.arpdev.plugin.config.GeneralConfig
import xyz.arpdev.plugin.signature.model.FailureReason
import xyz.arpdev.plugin.signature.model.ValidationResult
import com.google.inject.Inject
import com.google.inject.Singleton
import org.bukkit.inventory.ItemStack

@Singleton
class SignatureValidator @Inject constructor(
    private val config: GeneralConfig
) {

    fun validate(item: ItemStack, signature: String): ValidationResult {
        val restrictions = config.restrictions

        if (signature.length !in restrictions.minCharacters..restrictions.maxCharacters) return ValidationResult.Failure(FailureReason.INVALID_LENGTH)

        if (item.type.isAir) return ValidationResult.Failure(FailureReason.NO_ITEM)

        if (!isAllowedItem(item)) return ValidationResult.Failure(FailureReason.BLACKLISTED_ITEM)

        if (!isAllowedSignature(signature)) return ValidationResult.Failure(FailureReason.INVALID_SIGNATURE)

        return ValidationResult.Success
    }

    private fun isAllowedItem(item: ItemStack): Boolean {
        val filter = config.restrictions.itemFilter

        return when (filter.mode) {
            GeneralConfig.FilterMode.BLACKLIST -> item.type.name !in filter.materials
            GeneralConfig.FilterMode.WHITELIST -> item.type.name in filter.materials
        }
    }

    private fun isAllowedSignature(signature: String): Boolean {
        val filter = config.restrictions.signatureFilter

        val matched = filter.words.any { signature.contains(it, true) } || filter.regexes.any { Regex(it).containsMatchIn(signature) }

        return when (filter.mode) {
            GeneralConfig.FilterMode.BLACKLIST -> !matched
            GeneralConfig.FilterMode.WHITELIST -> matched
        }
    }
}
