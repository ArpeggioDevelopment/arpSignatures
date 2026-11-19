package xyz.arpdev.plugin.signature.format

import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.junit.Test
import xyz.arpdev.plugin.config.GeneralConfig
import xyz.arpdev.plugin.signature.model.Signature
import java.lang.reflect.Proxy
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

class SignatureFormatterTest {

    private val config = GeneralConfig()
    private val resolver = SignatureFormatResolver()

    @Test
    fun `skips default and uses the first matching permission`() {
        config.permissions.formats = linkedMapOf(
            "default" to "fallback",
            "arpsignatures.svip" to "svip",
            "arpsignatures.vip" to "vip"
        )

        assertEquals("svip", resolver.resolve(playerWithPermissions("arpsignatures.vip", "arpsignatures.svip"), config))
        assertEquals("vip", resolver.resolve(playerWithPermissions("arpsignatures.vip"), config))
        assertEquals("fallback", resolver.resolve(playerWithPermissions(), config))

        config.permissions.formats = linkedMapOf(
            "arpsignatures.vip" to "vip",
            "arpsignatures.svip" to "svip",
            "default" to "fallback"
        )
        assertEquals("vip", resolver.resolve(playerWithPermissions("arpsignatures.vip", "arpsignatures.svip"), config))
    }

    @Test
    fun `preserves player text literally while resolving template placeholders`() {
        config.permissions.formats = linkedMapOf("default" to "<white><signature> | <player> | <date>")
        config.dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val signature = Signature(
            text = "<red>{DATE}<player>",
            author = "Alice",
            createdAt = LocalDateTime.of(2026, 11, 20, 12, 34)
        )

        val component = SignatureFormatter(resolver).format(playerWithPermissions(), signature, config)

        assertEquals("<red>{DATE}<player> | Alice | 20.11.2026", PlainTextComponentSerializer.plainText().serialize(component))
        assertEquals(TextDecoration.State.FALSE, component.decoration(TextDecoration.ITALIC))
    }

    private fun playerWithPermissions(vararg permissions: String): Player {
        return Proxy.newProxyInstance(Player::class.java.classLoader, arrayOf(Player::class.java)) { _, method, args ->
            when (method.name) {
                "hasPermission" -> args?.firstOrNull() in permissions
                else -> error("Unexpected player call: ${method.name}")
            }
        } as Player
    }
}
