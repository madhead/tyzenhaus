package me.madhead.tyzenhaus.repository.postgresql.dialog.state

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import java.sql.ResultSet

internal fun ResultSet.toDialogState(json: Json): DialogState? {
    return if (this.next()) {
        json.decodeFromString<DialogState>(this.getString(@Suppress("MagicNumber") 3))
    } else {
        null
    }
}
