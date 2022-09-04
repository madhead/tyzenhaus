package me.madhead.tyzenhaus.repository.postgresql.dialog.state

import java.sql.ResultSet
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.madhead.tyzenhaus.entity.dialog.state.DialogState

internal fun ResultSet.toDialogState(json: Json): DialogState? {
    return if (this.next()) {
        json.decodeFromString<DialogState>(this.getString(@Suppress("MagicNumber") 3))
    } else {
        null
    }
}
