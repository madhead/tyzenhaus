package me.madhead.tyzenhaus.entity.serializers

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
internal class BigDecimalSerializerTest {
    @MockK
    private lateinit var encoder: Encoder

    @MockK
    private lateinit var decoder: Decoder

    private lateinit var sut: BigDecimalSerializer

    @BeforeEach
    fun setUp() {
        sut = BigDecimalSerializer()
    }

    @Test
    fun getDescriptor() {
        val expected = PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)
        val actual = sut.descriptor

        Assertions.assertEquals(expected.kind, actual.kind)
        Assertions.assertEquals(expected.serialName, actual.serialName)
    }

    @Test
    fun serialize() {
        every { encoder.encodeString("10.00") } just runs

        sut.serialize(encoder, BigDecimal("10.00"))

        verify { encoder.encodeString("10.00") }

        clearAllMocks()
    }

    @Test
    fun deserialize() {
        every { decoder.decodeString() } returns "10.00"

        Assertions.assertEquals(
            BigDecimal("10.00"),
            sut.deserialize(decoder)
        )

        verify { decoder.decodeString() }

        clearAllMocks()
    }
}
