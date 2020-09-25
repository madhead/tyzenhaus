package me.madhead.tyzenhaus.i18

import java.text.MessageFormat
import java.util.Locale
import java.util.ResourceBundle

/**
 * I18N entry point.
 */
open class I18N(
        private val locale: Locale,
        private val resourceBundle: ResourceBundle,
) {
    companion object {
        /**
         * Get an I18N instance for a given locale. [Default locale][Locale.getDefault] is used, when null passed.
         */
        operator fun invoke(locale: Locale? = null): I18N {
            val theLocale = locale ?: Locale.getDefault()

            return I18N(
                    locale = theLocale,
                    resourceBundle = ResourceBundle.getBundle("i18n", theLocale)
            )
        }
    }

    /**
     * Get a string for a given key.
     */
    operator fun get(key: String): String = resourceBundle.getString(key)

    /**
     * Get a string for template for a given key and a set of arguments.
     */
    operator fun get(key: String, vararg arguments: Any?): String =
            MessageFormat(this[key], this.locale).format(arguments, StringBuffer(), null).toString()
}
