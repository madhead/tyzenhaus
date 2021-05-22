package me.madhead.tyzenhaus.i18

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.Locale

@TestInstance(Lifecycle.PER_CLASS)
class I18NTest {
    private lateinit var defaultLocale: Locale

    @BeforeAll
    fun init() {
        defaultLocale = Locale.getDefault()
        Locale.setDefault(Locale.ENGLISH)
    }

    @ParameterizedTest
    @MethodSource("params")
    fun `I18N should provide correct string`(
            key: String,
            locale: Locale?,
            arguments: Array<Any?>,
            expected: String
    ) {
        Assertions.assertEquals(
                expected,
                if (null == locale) {
                    if (arguments.isEmpty()) I18N()[key] else I18N().get(key, *arguments)
                } else {
                    if (arguments.isEmpty()) I18N(locale)[key] else I18N(locale).get(key, *arguments)
                }
        )
    }

    @AfterAll
    fun deinit() {
        Locale.setDefault(defaultLocale)
    }

    @Suppress("unused")
    private fun params() = listOf(
            Arguments.of("message", Locale("ru"), emptyArray<Any?>(), "Сообщение"),
            Arguments.of("message", Locale("ru"), arrayOf(42), "Сообщение"),
            Arguments.of("message", Locale("ru", "BY"), emptyArray<Any?>(), "Сообщение"),
            Arguments.of("message", Locale("en"), emptyArray<Any?>(), "Message"),
            Arguments.of("message", null, emptyArray<Any?>(), "Message"),
            Arguments.of("message", Locale("en", "US"), emptyArray<Any?>(), "Message"),
            Arguments.of("message", Locale("en"), arrayOf(42), "Message"),
            Arguments.of("message", Locale("de"), arrayOf(42), "Message"),
            Arguments.of("message.extra", Locale("ru"), emptyArray<Any?>(), "Extra"),
            Arguments.of("message.extra", Locale("en"), emptyArray<Any?>(), "Extra"),
            Arguments.of(
                    "message.formatted",
                    Locale("en"),
                    arrayOf(3, "a disturbance in the Force"),
                    "There was a disturbance in the Force on planet 3"
            ),
            Arguments.of(
                    "message.formatted",
                    Locale("de"),
                    arrayOf(3, "a disturbance in the Force"),
                    "There was a disturbance in the Force on planet 3"
            ),
            Arguments.of(
                    "message.formatted",
                    Locale("ru"),
                    arrayOf(3, "возмущение в Силе"),
                    "На планете 3 было возмущение в Силе"
            ),
    ).stream()
}
