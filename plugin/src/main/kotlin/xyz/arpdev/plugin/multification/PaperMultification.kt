package xyz.arpdev.plugin.multification

import xyz.arpdev.plugin.config.ConfigService
import xyz.arpdev.plugin.config.MessagesConfig
import com.eternalcode.multification.adventure.AudienceConverter
import com.eternalcode.multification.notice.Notice
import com.eternalcode.multification.notice.NoticePart
import com.eternalcode.multification.notice.resolver.text.TextContent
import com.eternalcode.multification.paper.PaperMultification
import com.eternalcode.multification.translation.TranslationProvider
import com.google.inject.Inject
import com.google.inject.Singleton
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender

@Singleton
class PaperMultification @Inject constructor(
    private val configService: ConfigService
) : PaperMultification<MessagesConfig>() {

    private val placeholderPattern = Regex("\\{[A-Z0-9_]+}")

    override fun translationProvider() = TranslationProvider<MessagesConfig> { configService.current.messages }

    override fun serializer() = MiniMessage.miniMessage()

    override fun audienceConverter() = AudienceConverter<CommandSender?> { it }

    fun notify(
        sender: CommandSender,
        notice: Notice,
        vararg placeholders: Pair<String, String>,
    ) {
        create()
            .viewer(sender)
            .notice(formatNotice(notice, *placeholders))
            .send()
    }

    internal fun formatNotice(notice: Notice, vararg placeholders: Pair<String, String>): Notice {
        if (placeholders.isEmpty()) return notice

        val replacements = placeholders.associate { (key, value) -> key to serializer().escapeTags(value) }
        val builder = Notice.builder()

        for (part in notice.parts()) {
            val formatted = if (part.content() is TextContent) {
                @Suppress("UNCHECKED_CAST")
                getNoticeRegistry().applyText(part as NoticePart<TextContent>) { text ->
                    placeholderPattern.replace(text) { match -> replacements[match.value] ?: match.value }
                }
            } else part

            builder.withPart(formatted)
        }

        return builder.build()
    }
}
