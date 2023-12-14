package dk.gls.kemdk.model

data class RetryConfiguration(
    val maxAttempts: Int = 20,
    val timeoutMs: Long = 5000
)