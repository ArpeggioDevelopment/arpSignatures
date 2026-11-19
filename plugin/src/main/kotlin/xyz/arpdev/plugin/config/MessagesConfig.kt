package xyz.arpdev.plugin.config

import com.eternalcode.multification.notice.Notice
import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment

class MessagesConfig : OkaeriConfig() {
    var messages = Messages()

    class Messages : OkaeriConfig() {
        @Comment("https://eternalcode.pl/docs/multification/format")
        var invalidUsage: Notice = Notice.chat("<red>Usage: {USAGE}")
        var noPermission: Notice = Notice.chat("<red>You don't have permission to execute this command. ({PERMISSIONS})")
        @Comment("{SIGNATURE}, {MIN}, {MAX}")
        var notInRange: Notice = Notice.chat(
            "<red>Signature <dark_gray>(<white>{SIGNATURE}</white>)</dark_gray> must be in range between <dark_red>{MIN}-{MAX}</dark_red> characters.</red>"
        )
        var noItem: Notice = Notice.chat("<red>You can't sign air!")
        var blacklistedItem: Notice = Notice.chat("<red>You can't sign this item.")
        var invalidSignature: Notice = Notice.chat("<red>You can't use that signature!")
        @Comment("{SIGNATURE}, {DATE}")
        var signed: Notice = Notice.chat("<green>Successfully signed your item with signature '{SIGNATURE}' at {DATE}. ")

        var reloading: Notice = Notice.chat("<gray>Reloading configuration...")
        var reloaded: Notice = Notice.chat("<green>Successfully reloaded configuration in {TIME}ms!")
        var reloadError: Notice = Notice.chat("<red>There was an error trying to reload configuration. Check server logs for more information.")
    }
}
