package xyz.arpdev.plugin.multification

import com.eternalcode.multification.notice.Notice
import com.eternalcode.multification.notice.resolver.text.TextContent
import dev.rollczi.litecommands.handler.result.ResultHandlerChain
import dev.rollczi.litecommands.invocation.Invocation
import dev.rollczi.litecommands.permission.MissingPermissions
import dev.rollczi.litecommands.platform.PlatformSender
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.command.CommandSender
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import xyz.arpdev.plugin.command.handler.DefaultMissingPermissionHandler
import xyz.arpdev.plugin.config.ConfigService
import xyz.arpdev.plugin.config.GeneralConfigLoader
import xyz.arpdev.plugin.config.MessagesConfigLoader
import java.lang.reflect.Proxy
import kotlin.test.assertEquals
import kotlin.test.assertSame

class PaperMultificationTest {

    @get:Rule
    val folder = TemporaryFolder()

    private val received = mutableListOf<String>()

    @Test
    fun `signature placeholders remain literal regardless of replacement order`() {
        val multification = PaperMultification(configService())
        val notice = Notice.chat("{SIGNATURE} at {DATE}")
        val signature = "<red>{DATE}</red> {SIGNATURE}"
        val placeholders = arrayOf("{SIGNATURE}" to signature, "{DATE}" to "2026")

        multification.notify(sender(), notice, *placeholders)
        multification.notify(sender(), notice, *placeholders.reversedArray())

        assertEquals(listOf("$signature at 2026", "$signature at 2026"), received)
    }

    @Test
    fun `validation messages preserve user placeholders and special characters`() {
        val multification = PaperMultification(configService())
        val signature = "{MIN}{MAX} ${'$'}1 \\ <blue>"

        multification.notify(
            sender(),
            Notice.chat("<green>{MIN}-{MAX}: {SIGNATURE} {UNKNOWN}"),
            "{SIGNATURE}" to signature,
            "{MIN}" to "3",
            "{MAX}" to "32"
        )

        assertEquals(listOf("3-32: $signature {UNKNOWN}"), received)
    }

    @Test
    fun `formats all text channels without changing the original notice or sound`() {
        val multification = PaperMultification(configService())
        val notice = Notice.builder()
            .chat("chat {SIGNATURE}")
            .actionBar("actionbar {SIGNATURE}")
            .title("title {SIGNATURE}")
            .sound("minecraft:entity.player.levelup")
            .build()

        val formatted = multification.formatNotice(notice, "{SIGNATURE}" to "{DATE}", "{DATE}" to "2026")
        val textParts = formatted.parts().mapNotNull { it.content() as? TextContent }

        assertEquals(listOf("chat {DATE}", "actionbar {DATE}", "title {DATE}"), textParts.flatMap { it.contents() })
        assertEquals("chat {SIGNATURE}", (notice.parts().first().content() as TextContent).contents().single())
        assertSame(notice.parts().last().content(), formatted.parts().last().content())
        assertSame(notice, multification.formatNotice(notice))
    }

    @Test
    @Suppress("DEPRECATION")
    fun `missing permissions are reported to non-player senders`() {
        val config = configService()
        val commandSender = sender()
        val platformSender = Proxy.newProxyInstance(
            PlatformSender::class.java.classLoader,
            arrayOf(PlatformSender::class.java)
        ) { _, method, _ ->
            when (method.name) {
                "getHandle" -> commandSender
                else -> error("Unexpected platform call: ${method.name}")
            }
        } as PlatformSender
        val chain = object : ResultHandlerChain<CommandSender> {
            override fun <T> resolve(invocation: Invocation<CommandSender>, result: T, typeAs: Class<in T>) {
                error("Unexpected result handler call")
            }
        }

        DefaultMissingPermissionHandler(config, PaperMultification(config)).handle(
            Invocation(platformSender, "sign", "sign", null),
            MissingPermissions.missing("arpsignatures.use"),
            chain
        )

        assertEquals(listOf("You don't have permission to execute this command. (arpsignatures.use)"), received)
    }

    private fun configService() = ConfigService(folder.root, GeneralConfigLoader(), MessagesConfigLoader())

    private fun sender(): CommandSender {
        return Proxy.newProxyInstance(CommandSender::class.java.classLoader, arrayOf(CommandSender::class.java)) { proxy, method, args ->
            when (method.name) {
                "hashCode" -> System.identityHashCode(proxy)
                "equals" -> proxy === args?.firstOrNull()
                "toString" -> "Test command sender"
                "sendMessage" -> {
                    args.orEmpty().filterIsInstance<Component>().forEach { component ->
                        received += PlainTextComponentSerializer.plainText().serialize(component)
                    }
                    null
                }
                else -> error("Unexpected sender call: ${method.name}")
            }
        } as CommandSender
    }
}
