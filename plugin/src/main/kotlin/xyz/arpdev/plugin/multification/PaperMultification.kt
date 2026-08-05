package xyz.arpdev.plugin.multification

import xyz.arpdev.plugin.config.MessagesConfig
import com.eternalcode.multification.adventure.AudienceConverter
import com.eternalcode.multification.notice.Notice
import com.eternalcode.multification.paper.PaperMultification
import com.eternalcode.multification.translation.TranslationProvider
import com.google.inject.Inject
import com.google.inject.Singleton
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender

@Singleton
class PaperMultification @Inject constructor(
    private val generalConfig: MessagesConfig
) : PaperMultification<MessagesConfig>() {

    override fun translationProvider() = TranslationProvider<MessagesConfig> { generalConfig }

    override fun serializer() = MiniMessage.miniMessage()

    override fun audienceConverter() = AudienceConverter<CommandSender?> { it }

    fun notify(
        sender: CommandSender,
        notice: Notice,
        vararg placeholders: Pair<String, String>,
    ) {
        create()
            .viewer(sender)
            .notice(notice)
            .apply {
                placeholders.forEach { (key, value) ->
                    placeholder(key, value)
                }
            }
            .send()
    }
}
