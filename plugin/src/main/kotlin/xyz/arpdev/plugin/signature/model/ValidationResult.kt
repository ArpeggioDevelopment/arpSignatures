package xyz.arpdev.plugin.signature.model

sealed interface ValidationResult {
    data object Success : ValidationResult

    data class Failure(val reason: FailureReason) : ValidationResult
}

enum class FailureReason {
    INVALID_LENGTH,
    NO_ITEM,
    BLACKLISTED_ITEM,
    INVALID_SIGNATURE
}
