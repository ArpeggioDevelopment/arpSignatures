package xyz.arpdev.plugin.config

import org.bukkit.Material
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeneralConfigLoaderTest {

    @get:Rule
    val folder = TemporaryFolder()

    private val loader = GeneralConfigLoader()

    @Test
    fun `creates valid defaults without persisting prepared settings`() {
        val file = folder.root.resolve("general.yml")
        val config = loader.load(file)

        assertEquals("default", config.permissions.formats.keys.first())
        assertTrue(config.restrictions.signatureFilter.compiledRegexes.isEmpty())
        assertEquals(setOf(Material.TRIPWIRE_HOOK), config.restrictions.itemFilter.resolvedMaterials)
        assertEquals("12:34:56 19.11.2026", LocalDateTime.of(2026, 11, 19, 12, 34, 56).format(config.dateFormatter))

        config.save()
        assertFalse(file.readText().contains("dateFormatter"))
        assertFalse(file.readText().contains("compiledRegexes"))
        assertFalse(file.readText().contains("resolvedMaterials"))
    }

    @Test
    fun `rejects invalid configuration with the setting name`() {
        val invalidSettings = listOf(
            "restrictions:\n  itemFilter:\n    materials: ['DIAMOND_SOWRD']" to "restrictions.itemFilter.materials[0]",
            "restrictions:\n  itemFilter:\n    materials: ['STONE', '']" to "restrictions.itemFilter.materials[1]",
            "restrictions:\n  signatureFilter:\n    regexes: ['[']" to "restrictions.signatureFilter.regexes[0]",
            "dateFormat: 'invalid-pattern'" to "dateFormat",
            "dateFormat: 'yyyy-MM-dd XXX'" to "dateFormat",
            "dateFormat: ''" to "dateFormat",
            "permissions:\n  formats:\n    custom: '<signature>'" to "permissions.formats",
            "permissions:\n  formats:\n    default: ' '" to "permissions.formats",
            "restrictions:\n  minCharacters: 0" to "restrictions.minCharacters",
            "restrictions:\n  minCharacters: -1" to "restrictions.minCharacters",
            "restrictions:\n  minCharacters: 10\n  maxCharacters: 3" to "restrictions.maxCharacters"
        )

        for ((yaml, setting) in invalidSettings) {
            val file = folder.newFile()
            file.writeText(yaml)

            val exception = assertFailsWith<IllegalArgumentException>(setting) {
                loader.load(file)
            }
            assertTrue(exception.message.orEmpty().contains(setting), exception.message)
        }
    }

    @Test
    fun `resolves materials without duplicates and supports an empty filter`() {
        val file = folder.newFile()
        file.writeText("restrictions:\n  itemFilter:\n    materials: ['STONE', 'DIAMOND_SWORD', 'STONE']")

        val config = loader.load(file)
        assertEquals(setOf(Material.STONE, Material.DIAMOND_SWORD), config.restrictions.itemFilter.resolvedMaterials)

        file.writeText("restrictions:\n  itemFilter:\n    materials: []")
        assertTrue(loader.load(file).restrictions.itemFilter.resolvedMaterials.isEmpty())
    }

    @Test
    fun `prepares regexes and date formatter for each load`() {
        val file = folder.newFile()
        file.writeText("restrictions:\n  signatureFilter:\n    regexes: ['(?i)blocked']")
        var config = loader.load(file)
        val previousRegex = config.restrictions.signatureFilter.compiledRegexes.single()
        assertTrue(previousRegex.containsMatchIn("BLOCKED text"))

        file.writeText("dateFormat: 'yyyy'\nrestrictions:\n  signatureFilter:\n    regexes: ['^allowed$']")
        config = loader.load(file)

        val regex = config.restrictions.signatureFilter.compiledRegexes.single()
        assertTrue(regex.matches("allowed"))
        assertFalse(regex.containsMatchIn("BLOCKED text"))
        assertEquals("2026", LocalDateTime.of(2026, 11, 19, 12, 34).format(config.dateFormatter))

        file.writeText("restrictions:\n  signatureFilter:\n    regexes: []")
        config = loader.load(file)
        assertTrue(config.restrictions.signatureFilter.compiledRegexes.isEmpty())
    }
}
