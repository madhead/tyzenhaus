package me.madhead.tyzenhaus.launcher.fly.security

import java.time.Instant
import korlibs.crypto.HMAC
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class APITest {
    private val botToken = "123456:TEST-bot-token"
    private val secretKeyHash = HMAC.hmacSHA256("WebAppData".toByteArray(), botToken.toByteArray()).bytes
    private val json = Json

    @Test
    fun `valid, fresh initData returns the user id`() {
        val initData = sign(listOf("auth_date=${Instant.now().epochSecond}", """user={"id":42,"first_name":"Test"}"""))

        assertEquals(42L, initData.validate(secretKeyHash, json))
    }

    @Test
    fun `a tampered user id is rejected`() {
        val signed = sign(listOf("auth_date=${Instant.now().epochSecond}", """user={"id":42}"""))
        val tampered = signed.replace(""""id":42""", """"id":99""")

        assertNull(tampered.validate(secretKeyHash, json))
    }

    @Test
    fun `initData signed with a different token is rejected`() {
        val initData = sign(listOf("auth_date=${Instant.now().epochSecond}", """user={"id":42}"""))
        val otherSecret = HMAC.hmacSHA256("WebAppData".toByteArray(), "999:OTHER".toByteArray()).bytes

        assertNull(initData.validate(otherSecret, json))
    }

    @Test
    fun `initData without a hash is rejected`() {
        val initData = """auth_date=${Instant.now().epochSecond}&user={"id":42}"""

        assertNull(initData.validate(secretKeyHash, json))
    }

    @Test
    fun `correctly signed initData without a user is rejected`() {
        val initData = sign(listOf("auth_date=${Instant.now().epochSecond}"))

        assertNull(initData.validate(secretKeyHash, json))
    }

    private fun sign(fields: List<String>): String {
        val hash = HMAC.hmacSHA256(secretKeyHash, fields.sorted().joinToString("\n").toByteArray()).hexLower

        return (fields + "hash=$hash").joinToString("&")
    }
}
