package xyz.arpdev.plugin.signature.format

import xyz.arpdev.plugin.config.GeneralConfig
import com.google.inject.Inject
import com.google.inject.Singleton
import org.bukkit.entity.Player

@Singleton
class SignatureFormatResolver @Inject constructor(
    private val generalConfig: GeneralConfig
) {
    fun resolve(player: Player): String {
        val formats = generalConfig.permissions.formats

        for ((permission, format) in formats) {
            if (permission == "default") continue

            if (player.hasPermission(permission)) {
                return format
            }
        }

        return formats["default"] ?: error("Missing 'default' signature format.")
    }
}
