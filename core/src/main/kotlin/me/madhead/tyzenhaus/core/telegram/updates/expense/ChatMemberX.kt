package me.madhead.tyzenhaus.core.telegram.updates.expense

import dev.inmo.tgbotapi.types.chat.member.ChatMember

internal val ChatMember.displayName: String
    get() {
        return user.firstName + (user.lastName.takeUnless { it.isBlank() }?.let { " $it" } ?: "")
    }

internal val ChatMember.displayNameWithId: String
    get() {
        return (user.firstName +
            (user.lastName.takeUnless { it.isBlank() }?.let { " $it" } ?: "") +
            (user.username?.username?.let { " ($it)" } ?: ""))
    }
