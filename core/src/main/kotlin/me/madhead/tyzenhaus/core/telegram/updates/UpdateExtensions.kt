package me.madhead.tyzenhaus.core.telegram.updates

import com.github.insanusmokrassar.TelegramBotAPI.types.CallbackQuery.MessageCallbackQuery
import com.github.insanusmokrassar.TelegramBotAPI.types.message.CommonMessageImpl
import com.github.insanusmokrassar.TelegramBotAPI.types.update.CallbackQueryUpdate
import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.BaseMessageUpdate
import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.Update

val Update.chatId: Long
    get() = when (this) {
        is BaseMessageUpdate -> this.data.chat.id.chatId
        is CallbackQueryUpdate -> {
            when (val callbackQuery = this.data) {
                is MessageCallbackQuery -> callbackQuery.message.chat.id.chatId
                else -> throw IllegalArgumentException("Unknown update type")
            }
        }
        else -> throw IllegalArgumentException("Unknown update type")
    }

val Update.userId: Long
    get() = when (this) {
        is BaseMessageUpdate -> {
            when (val message = this.data) {
                is CommonMessageImpl<*> -> message.user.id.chatId
                else -> throw IllegalArgumentException("Unknown update type")
            }
        }
        is CallbackQueryUpdate -> this.data.user.id.chatId
        else -> throw IllegalArgumentException("Unknown update type")
    }
