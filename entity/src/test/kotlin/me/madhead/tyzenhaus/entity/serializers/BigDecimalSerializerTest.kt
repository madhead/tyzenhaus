package me.madhead.tyzenhaus.entity.serializers

import org.apache.commons.validator.routines.InetAddressValidator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


internal class IPAddressValidationTest {
    @ParameterizedTest
    @ValueSource(strings = [
        "1.2.3.4",
        "01.102.103.104",
        "2001:db8:3333:4444:5555:6666:7777:8888",
        "2001:db8:3333:4444:CCCC:DDDD:EEEE:FFFF",
        "::",
        "2001:db8::",
        "::1234:5678",
        "2001:db8::1234:5678",
        "2001:0db8:0001:0000:0000:0ab9:C0A8:0102",
        "2001:db8:1::ab9:C0A8:102",
        "2001:db8:3333:4444:5555:6666:1.2.3.4",
        "::11.22.33.44",
        "2001:db8::123.123.123.123",
        "::1234:5678:91.123.4.56",
        "::1234:5678:1.2.3.4",
        "2001:db8::1234:5678:5.6.7.8",
    ])
    fun test(value: String) {
        assertTrue(isValidIP(value))
    }
}


val validator = InetAddressValidator.getInstance()

fun isValidIP(value: String): Boolean {
    return validator.isValid(value)
}