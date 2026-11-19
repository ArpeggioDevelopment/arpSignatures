package xyz.arpdev.plugin.signature.format

import xyz.arpdev.plugin.config.GeneralConfig
import com.google.inject.Singleton
import org.bukkit.entity.Player

@Singleton
class SignatureFormatResolver {
    fun resolve(player: Player, config: GeneralConfig): String {
        val formats = config.permissions.formats

        for ((permission, format) in formats) {
            if (permission == "default") continue

            if (player.hasPermission(permission)) {
                return format
            }
        }

        return formats["default"] ?: error("Missing 'default' signature format.")
    }
}
