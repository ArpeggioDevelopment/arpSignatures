package xyz.arpdev.plugin.config

import com.eternalcode.multification.notice.resolver.text.TextContent
import org.bukkit.Material
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ConfigServiceTest {

    @get:Rule
    val folder = TemporaryFolder()

    @Test
    fun `publishes both files together and preserves the previous snapshot`() {
        val service = createService()
        val previous = service.current
        val previousText = signedText(previous)

        generalFile().writeText("dateFormat: 'yyyy'\nrestrictions:\n  itemFilter:\n    materials: ['STONE']\n  signatureFilter:\n    regexes: ['^allowed$']")
        writeMessages("updated")
        val loaded = service.reload()

        assertSame(loaded, service.current)
        assertNotSame(previous.general, loaded.general)
        assertNotSame(previous.messages, loaded.messages)
        assertEquals("updated", signedText(loaded))
        assertTrue(loaded.general.restrictions.signatureFilter.compiledRegexes.single().matches("allowed"))
        assertEquals(setOf(Material.STONE), loaded.general.restrictions.itemFilter.resolvedMaterials)
        assertEquals("2026", LocalDateTime.of(2026, 11, 19, 12, 34).format(loaded.general.dateFormatter))
        assertEquals("HH:mm:ss dd.MM.yyyy", previous.general.dateFormat)
        assertTrue(previous.general.restrictions.signatureFilter.compiledRegexes.isEmpty())
        assertEquals(setOf(Material.TRIPWIRE_HOOK), previous.general.restrictions.itemFilter.resolvedMaterials)
        assertEquals(previousText, signedText(previous))
    }

    @Test
    fun `keeps both active configs when messages file is malformed`() {
        val service = createService()
        val previous = service.current

        generalFile().writeText("dateFormat: 'yyyy'")
        messagesFile().writeText("messages: [")

        assertFailsWith<Exception> { service.reload() }
        assertSame(previous, service.current)
        assertEquals("HH:mm:ss dd.MM.yyyy", service.current.general.dateFormat)
        assertEquals("messages: [", messagesFile().readText())

        writeMessages("recovered")
        assertEquals("recovered", signedText(service.reload()))
        assertEquals("yyyy", service.current.general.dateFormat)
    }

    @Test
    fun `keeps both active configs when general file fails parsing or validation`() {
        val service = createService()
        val previous = service.current
        writeMessages("pending")

        for (yaml in listOf(
            "dateFormat: [",
            "dateFormat: 'yyyy'\nrestrictions:\n  signatureFilter:\n    regexes: ['[']",
            "restrictions:\n  itemFilter:\n    materials: ['DIAMOND_SOWRD']"
        )) {
            generalFile().writeText(yaml)
            assertFailsWith<Exception> { service.reload() }
            assertSame(previous, service.current)
            assertEquals(yaml, generalFile().readText())
        }
    }

    @Test
    fun `rejects invalid message structure without changing active settings`() {
        val service = createService()
        val previous = service.current
        generalFile().writeText("dateFormat: 'yyyy'")

        for (yaml in listOf("messages: 123", "messages:\n  reloaded:\n    bossbar:\n      color: INVALID")) {
            messagesFile().writeText(yaml)
            assertFailsWith<Exception>(yaml) { service.reload() }
            assertSame(previous, service.current)
        }
    }

    @Test
    fun `removed settings return to defaults after reload`() {
        val service = createService()
        val defaults = signedText(service.current)
        generalFile().writeText("dateFormat: 'yyyy'\nrestrictions:\n  signatureFilter:\n    regexes: ['blocked']")
        writeMessages("custom")
        service.reload()

        generalFile().writeText("{}")
        messagesFile().writeText("{}")
        val loaded = service.reload()

        assertEquals("HH:mm:ss dd.MM.yyyy", loaded.general.dateFormat)
        assertTrue(loaded.general.restrictions.signatureFilter.compiledRegexes.isEmpty())
        assertEquals(defaults, signedText(loaded))
    }

    @Test
    fun `concurrent readers see matching general settings and messages`() {
        val service = createService()
        generalFile().writeText("dateFormat: 'yyyy'")
        writeMessages("yyyy")
        service.reload()

        val running = AtomicBoolean(true)
        val started = CountDownLatch(1)
        val executor = Executors.newSingleThreadExecutor()
        val reader = executor.submit {
            do {
                val snapshot = service.current
                assertEquals(snapshot.general.dateFormat, signedText(snapshot))
                started.countDown()
            } while (running.get())
        }

        try {
            assertTrue(started.await(5, TimeUnit.SECONDS))
            repeat(30) { index ->
                val format = if (index % 2 == 0) "MM" else "yyyy"
                generalFile().writeText("dateFormat: '$format'")
                writeMessages(format)
                service.reload()
            }
        } finally {
            running.set(false)
            executor.shutdown()
        }

        reader.get(5, TimeUnit.SECONDS)
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS))
    }

    private fun createService() = ConfigService(folder.root, GeneralConfigLoader(), MessagesConfigLoader())

    private fun generalFile() = folder.root.resolve("general.yml")

    private fun messagesFile() = folder.root.resolve("messages.yml")

    private fun writeMessages(text: String) {
        messagesFile().writeText("messages:\n  signed:\n    chat: ['$text']")
    }

    private fun signedText(snapshot: ConfigSnapshot): String {
        val content = snapshot.messages.messages.signed.parts().single().content() as TextContent
        return content.contents().single()
    }
}
