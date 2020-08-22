package me.madhead.tyzenhaus.i18

/**
 * Typed accessor for i18n messages stored in property files.
 */
@Suppress("UndocumentedPublicFunction")
interface Messages {
    fun welcome(): String

    fun help(): String

    fun language(): String
    fun languageOk(): String
    fun languageWrongUser(): String
}
