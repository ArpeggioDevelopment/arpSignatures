package xyz.arpdev.plugin.config

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment

class GeneralConfig : OkaeriConfig() {
    var dateFormat = "HH:mm:ss dd.MM.yyyy"

    var restrictions = Restrictions()
    var permissions = Permissions()

    class Restrictions : OkaeriConfig() {
        var minCharacters = 3
        var maxCharacters = 32

        var itemFilter = ItemFilter()
        var signatureFilter = SignatureFilter()
    }

    class Permissions : OkaeriConfig() {
        @Comment(
            "<signature>, <player>, <date>",
            "Permissions are checked from top to bottom. If a player has multiple permissions, the first matching entry will be used."
        )
        var formats = linkedMapOf(
            "default" to "<white><signature> <dark_gray>(<gray><player>, <date></gray>)",
            "arpsignatures.rainbow" to "<rainbow<signature> <gold>(<yellow><player>, <date></yellow>)",
            "arpsignatures.svip" to "<aqua><signature> <dark_gray>(<gray><player>, <date></gray>)",
            "arpsignatures.vip" to "<yellow><signature> <dark_gray>(<gray><player>, <date></gray>)"
        )
    }

    class ItemFilter : OkaeriConfig() {
        @Comment("WHITELIST or BLACKLIST") var mode = FilterMode.BLACKLIST
        var materials = mutableListOf(
            "TRIPWIRE_HOOK"
        )
    }

    class SignatureFilter : OkaeriConfig() {
        var mode = FilterMode.BLACKLIST

        var words = mutableListOf(
            "{",
            "}"
        )

        var regexes = mutableListOf(
            ""
        )
    }

    enum class FilterMode {
        BLACKLIST,
        WHITELIST
    }
}
