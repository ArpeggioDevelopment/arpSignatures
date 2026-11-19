package xyz.arpdev.plugin.signature.validation

import xyz.arpdev.plugin.config.GeneralConfig
import xyz.arpdev.plugin.signature.model.FailureReason
import xyz.arpdev.plugin.signature.model.ValidationResult
import com.google.inject.Singleton
import org.bukkit.inventory.ItemStack

@Singleton
class SignatureValidator {

    fun validate(item: ItemStack, signature: String, config: GeneralConfig): ValidationResult {
        val restrictions = config.restrictions

        if (signature.length !in restrictions.minCharacters..restrictions.maxCharacters) return ValidationResult.Failure(FailureReason.INVALID_LENGTH)

        if (item.type.isAir) return ValidationResult.Failure(FailureReason.NO_ITEM)

        if (!isAllowedItem(item, restrictions.itemFilter)) return ValidationResult.Failure(FailureReason.BLACKLISTED_ITEM)

        if (!isAllowedSignature(signature, restrictions.signatureFilter)) return ValidationResult.Failure(FailureReason.INVALID_SIGNATURE)

        return ValidationResult.Success
    }

    private fun isAllowedItem(item: ItemStack, filter: GeneralConfig.ItemFilter): Boolean {
        return when (filter.mode) {
            GeneralConfig.FilterMode.BLACKLIST -> item.type !in filter.resolvedMaterials
            GeneralConfig.FilterMode.WHITELIST -> item.type in filter.resolvedMaterials
        }
    }

    private fun isAllowedSignature(signature: String, filter: GeneralConfig.SignatureFilter): Boolean {
        val matched = filter.words.any { signature.contains(it, true) } || filter.compiledRegexes.any { it.containsMatchIn(signature) }

        return when (filter.mode) {
            GeneralConfig.FilterMode.BLACKLIST -> !matched
            GeneralConfig.FilterMode.WHITELIST -> matched
        }
    }
}
