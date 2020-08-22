package me.madhead.tyzenhaus.i18

import com.github.rodionmoiseev.c10n.C10N
import com.github.rodionmoiseev.c10n.C10NConfigBase
import java.util.Locale

object I18N {
    init {
        C10N.configure(object : C10NConfigBase() {
            override fun configure() {
                bindBundle("i18n")
            }
        })
    }

    fun messages(locale: Locale? = null): Messages = C10N.get(Messages::class.java, locale ?: Locale("en"))
}
