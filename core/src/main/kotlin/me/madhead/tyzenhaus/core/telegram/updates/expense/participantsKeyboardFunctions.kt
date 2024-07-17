package me.madhead.tyzenhaus.core.telegram.updates.expense

import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.chat.member.ChatMember
import java.util.Locale
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N

internal fun replyMarkup(chatMembers: List<ChatMember>, participants: Set<Long>, groupConfig: GroupConfig, withDone: Boolean = true) =
    InlineKeyboardMarkup(
        chatMembers
            .map {
                listOf(
                    CallbackDataInlineKeyboardButton(
                        it.callbackText(participants, groupConfig.language),
                        it.callbackData()
                    )
                )
            }
            +
            if (withDone) {
                listOf(
                    listOf(
                        CallbackDataInlineKeyboardButton(
                            I18N(groupConfig.language)["expense.response.participants.done"],
                            DoneCallbackQueryUpdateProcessor.CALLBACK,
                        )
                    )
                )
            } else {
                emptyList()
            }
    )

private fun ChatMember.callbackText(participants: Set<Long>, language: Locale?): String =
    if (this.user.id.chatId.long in participants) {
        I18N(language)["expense.response.participants.checked"]
    } else {
        I18N(language)["expense.response.participants.unchecked"]
    } + this.displayNameWithId

private fun ChatMember.callbackData(): String = "${ParticipantCallbackQueryUpdateProcessor.CALLBACK_PREFIX}${user.id.chatId}"
