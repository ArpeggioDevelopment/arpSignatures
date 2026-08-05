package xyz.arpdev.plugin.signature.model

import java.time.LocalDateTime

data class Signature(
    val text: String,
    val author: String,
    val createdAt: LocalDateTime
)
