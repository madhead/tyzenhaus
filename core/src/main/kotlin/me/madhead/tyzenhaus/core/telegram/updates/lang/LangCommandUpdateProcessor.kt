package me.madhead.tyzenhaus.core.telegram.updates.lang

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.message.MarkdownV2
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.textsources.BotCommandTextSource
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.core.telegram.updates.groupId
import me.madhead.tyzenhaus.core.telegram.updates.lang.LangCallbackQueryUpdateProcessor.Companion.CALLBACK_PREFIX
import me.madhead.tyzenhaus.core.telegram.updates.userId
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import org.apache.logging.log4j.LogManager

/**
 * /lang command handler.
 */
class LangCommandUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(LangCommandUpdateProcessor::class.java)!!
    }

    @Suppress("DuplicatedCode")
    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return null
        val message = update.data as? CommonMessage<*> ?: return null
        val content = (message as? CommonMessage<*>)?.content as? TextContent ?: return null

        if (content.textSources.none { "lang" == (it as? BotCommandTextSource)?.command }) return null

        return {
            logger.debug("{} initiated a language change in {}", update.userId, update.groupId)

            requestsExecutor.sendMessage(
                chatId = update.data.chat.id,
                text = I18N(groupConfig?.language)["language.action.choose"],
                parseMode = MarkdownV2,
                replyMarkup = InlineKeyboardMarkup(
                    listOf(
                        listOf(
                            CallbackDataInlineKeyboardButton("English", "${CALLBACK_PREFIX}en"),
                            CallbackDataInlineKeyboardButton("Italiano", "${CALLBACK_PREFIX}it"),
                            CallbackDataInlineKeyboardButton("Português", "${CALLBACK_PREFIX}pt"),
                            CallbackDataInlineKeyboardButton("Русский", "${CALLBACK_PREFIX}ru"),
                        )
                    )
                )
            )
        }
    }
}
