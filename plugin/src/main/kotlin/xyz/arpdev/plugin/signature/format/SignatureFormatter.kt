package xyz.arpdev.plugin.signature.format

import xyz.arpdev.plugin.config.GeneralConfig
import xyz.arpdev.plugin.signature.model.Signature
import com.google.inject.Inject
import com.google.inject.Singleton
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player

@Singleton
class SignatureFormatter @Inject constructor(
    private val formatResolver: SignatureFormatResolver
) {
    fun format(player: Player, signature: Signature, config: GeneralConfig): Component {
        val format = formatResolver.resolve(player, config)
        val date = signature.createdAt.format(config.dateFormatter)

        return MiniMessage.miniMessage().deserialize(
            format,
            Placeholder.unparsed("signature", signature.text),
            Placeholder.unparsed("player", signature.author),
            Placeholder.unparsed("date", date)
        ).decoration(TextDecoration.ITALIC, false)
    }
}
