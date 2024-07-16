package me.madhead.tyzenhaus.core.telegram.updates

import dev.inmo.tgbotapi.types.message.abstracts.FromUserMessage
import dev.inmo.tgbotapi.types.queries.callback.MessageCallbackQuery
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.MyChatMemberUpdatedUpdate
import dev.inmo.tgbotapi.types.update.abstracts.BaseMessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update

val Update.groupId: Long
    get() = when (this) {
        is BaseMessageUpdate -> this.data.chat.id.chatId.long
        is CallbackQueryUpdate -> {
            when (val callbackQuery = this.data) {
                is MessageCallbackQuery -> callbackQuery.message.chat.id.chatId.long
                else -> throw IllegalArgumentException("Unknown update type")
            }
        }

        is MyChatMemberUpdatedUpdate -> this.data.chat.id.chatId.long
        else -> throw IllegalArgumentException("Unknown update type")
    }

val Update.userId: Long
    get() = when (this) {
        is BaseMessageUpdate -> {
            when (val message = this.data) {
                is FromUserMessage -> message.user.id.chatId.long
                else -> throw IllegalArgumentException("Unknown update type")
            }
        }

        is CallbackQueryUpdate -> this.data.user.id.chatId.long
        else -> throw IllegalArgumentException("Unknown update type")
    }
