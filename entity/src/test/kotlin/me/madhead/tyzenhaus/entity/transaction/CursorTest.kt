package me.madhead.tyzenhaus.entity.transaction

import java.time.Instant
import java.util.Base64
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class CursorTest {
    @Test
    fun roundTrip() {
        val cursor = Cursor(Instant.parse("2024-01-05T12:00:00Z"), 105)

        assertEquals(cursor, Cursor.decode(cursor.encode()))
    }

    @Test
    fun roundTripPreservesSubSecondPrecision() {
        val cursor = Cursor(Instant.parse("2024-01-05T12:00:00.123456Z"), 42)

        assertEquals(cursor, Cursor.decode(cursor.encode()))
    }

    @Test
    fun encodeIsUrlSafeAndUnpadded() {
        val token = Cursor(Instant.parse("2024-01-05T12:00:00Z"), 105).encode()

        assertNull(token.firstOrNull { it == '+' || it == '/' || it == '=' })
    }

    @Test
    fun decodeRejectsGarbage() {
        assertNull(Cursor.decode("not-a-cursor"))
    }

    @Test
    fun decodeRejectsMissingSeparator() {
        assertNull(Cursor.decode(Base64.getUrlEncoder().withoutPadding().encodeToString("no-separator".toByteArray())))
    }

    @Test
    fun decodeRejectsNonNumericId() {
        assertNull(Cursor.decode(Base64.getUrlEncoder().withoutPadding().encodeToString("2024-01-05T12:00:00Z|abc".toByteArray())))
    }
}
