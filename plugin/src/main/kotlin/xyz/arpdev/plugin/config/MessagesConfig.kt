package xyz.arpdev.plugin.config

import com.eternalcode.multification.notice.Notice
import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment

class MessagesConfig : OkaeriConfig() {
    var messages = Messages()

    class Messages : OkaeriConfig() {
        @Comment("https://eternalcode.pl/docs/multification/format")
        var invalidUsage: Notice = Notice.builder()
            .chat("<red>Usage: {USAGE}")
            .build()
        var noPermission: Notice = Notice.builder()
            .chat("<red>You don't have permission to execute this command. ({PERMISSIONS})")
            .build()
        @Comment("{SIGNATURE}, {MIN}, {MAX}")
        var notInRange: Notice = Notice.builder()
            .chat("<red>Signature <dark_gray>(<white>{SIGNATURE}</white>)</dark_gray> must be in range between <dark_red>{MIN}-{MAX}</dark_red> characters.</red>")
            .build()
        var noItem: Notice = Notice.builder()
            .chat("<red>You can't sign air!")
            .build()
        var blacklistedItem: Notice = Notice.builder()
            .chat("<red>You can't sign this item.")
            .build()
        var invalidSignature: Notice = Notice.builder()
            .chat("<red>You can't use that signature!")
            .build()
        @Comment("{SIGNATURE}, {DATE}")
        var signed: Notice = Notice.builder()
            .chat("<green>Successfully signed your item with signature '{SIGNATURE}' at {DATE}. ")
            .build()

        var reloading: Notice = Notice.builder()
            .chat("<gray>Reloading configuration...")
            .build()
        var reloaded: Notice = Notice.builder()
            .chat("<green>Successfully reloaded configuration in {TIME}ms!")
            .build()
        var reloadError: Notice = Notice.builder()
            .chat("<red>There was an error trying to reload configuration. Check server logs for more information.")
            .build()
    }
}
