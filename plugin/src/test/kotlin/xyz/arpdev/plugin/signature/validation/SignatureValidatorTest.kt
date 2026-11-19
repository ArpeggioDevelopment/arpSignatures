package xyz.arpdev.plugin.signature.validation

import org.bukkit.Material
import org.junit.Test
import xyz.arpdev.plugin.config.GeneralConfig
import xyz.arpdev.plugin.signature.model.FailureReason
import xyz.arpdev.plugin.signature.model.ValidationResult
import kotlin.test.assertEquals

class SignatureValidatorTest {

    private val validator = SignatureValidator()
    private val config = GeneralConfig()

    @Test
    fun `accepts length boundaries and rejects text outside the range`() {
        for (length in listOf(2, 3, 32, 33)) {
            val expected = if (length in 3..32) ValidationResult.Success else failure(FailureReason.INVALID_LENGTH)
            assertEquals(expected, validate("a".repeat(length)), "Length: $length")
        }
    }

    @Test
    fun `rejects every air material`() {
        for (material in listOf(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR)) {
            assertEquals(failure(FailureReason.NO_ITEM), validate(material = material), material.name)
        }
    }

    @Test
    fun `applies item blacklist and whitelist to resolved materials`() {
        val filter = config.restrictions.itemFilter
        filter.resolvedMaterials = setOf(Material.DIAMOND_SWORD)

        for (mode in GeneralConfig.FilterMode.entries) {
            filter.mode = mode
            for (material in listOf(Material.DIAMOND_SWORD, Material.STONE)) {
                val allowed = (material == Material.DIAMOND_SWORD) == (mode == GeneralConfig.FilterMode.WHITELIST)
                val expected = if (allowed) ValidationResult.Success else failure(FailureReason.BLACKLISTED_ITEM)
                assertEquals(expected, validate(material = material), "$mode: $material")
            }
        }
    }

    @Test
    fun `matches words without case sensitivity and combines them with regexes`() {
        val filter = config.restrictions.signatureFilter
        filter.words = mutableListOf("blocked")
        filter.compiledRegexes = listOf(Regex("^test[0-9]+$"))

        for (mode in GeneralConfig.FilterMode.entries) {
            filter.mode = mode
            for ((text, matched) in listOf("aBLOCKEDb" to true, "test123" to true, "TEST123" to false, "valid" to false)) {
                val allowed = matched == (mode == GeneralConfig.FilterMode.WHITELIST)
                val expected = if (allowed) ValidationResult.Success else failure(FailureReason.INVALID_SIGNATURE)
                assertEquals(expected, validate(text), "$mode: $text")
            }
        }
    }

    @Test
    fun `empty blacklists allow signatures and empty whitelists reject them`() {
        assertEquals(ValidationResult.Success, validate())

        config.restrictions.itemFilter.mode = GeneralConfig.FilterMode.WHITELIST
        assertEquals(failure(FailureReason.BLACKLISTED_ITEM), validate())

        config.restrictions.itemFilter.mode = GeneralConfig.FilterMode.BLACKLIST
        config.restrictions.signatureFilter.mode = GeneralConfig.FilterMode.WHITELIST
        assertEquals(failure(FailureReason.INVALID_SIGNATURE), validate())
    }

    private fun validate(text: String = "valid", material: Material = Material.STONE) =
        validator.validate(material, text, config)

    private fun failure(reason: FailureReason) = ValidationResult.Failure(reason)
}
