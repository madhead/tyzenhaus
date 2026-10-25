package me.madhead.tyzenhaus.entity.transaction

import java.time.Instant
import java.util.Base64

/**
 * Stateless pagination cursor: the `(timestamp, id)` of the last-seen transaction under the `ORDER BY timestamp DESC, id DESC` order.
 *
 * The next page is everything "after" this row, i.e., the transactions whose `(timestamp, id)` compares less than the cursor's.
 */
data class Cursor(
    val timestamp: Instant,
    val id: Long,
) {
    /**
     * Encodes this cursor into an opaque, URL-safe token suitable for a query parameter.
     */
    fun encode(): String = Base64
        .getUrlEncoder()
        .withoutPadding()
        .encodeToString("$timestamp|$id".toByteArray(Charsets.UTF_8))

    companion object {
        /**
         * Decodes a token produced by [encode], or returns `null` if it is malformed.
         */
        fun decode(token: String): Cursor? =
            runCatching {
                val (timestamp, id) = String(Base64.getUrlDecoder().decode(token), Charsets.UTF_8).split("|", limit = 2)

                Cursor(Instant.parse(timestamp), id.toLong())
            }.getOrNull()
    }
}
