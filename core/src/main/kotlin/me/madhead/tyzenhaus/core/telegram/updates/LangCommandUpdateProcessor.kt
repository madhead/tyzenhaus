package me.madhead.tyzenhaus.core.telegram.updates

import com.github.insanusmokrassar.TelegramBotAPI.bot.RequestsExecutor
import com.github.insanusmokrassar.TelegramBotAPI.extensions.api.send.sendMessage
import com.github.insanusmokrassar.TelegramBotAPI.types.MessageEntity.textsources.BotCommandTextSource
import com.github.insanusmokrassar.TelegramBotAPI.types.ParseMode.MarkdownV2
import com.github.insanusmokrassar.TelegramBotAPI.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import com.github.insanusmokrassar.TelegramBotAPI.types.buttons.InlineKeyboardMarkup
import com.github.insanusmokrassar.TelegramBotAPI.types.message.CommonMessageImpl
import com.github.insanusmokrassar.TelegramBotAPI.types.message.content.TextContent
import com.github.insanusmokrassar.TelegramBotAPI.types.update.MessageUpdate
import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.Update
import me.madhead.tyzenhaus.entity.groupconfig.GroupConfig
import me.madhead.tyzenhaus.entity.groupstate.GroupState
import me.madhead.tyzenhaus.i18.I18N
import org.apache.logging.log4j.LogManager

/**
 * /lang command handler.
 */
class LangCommandUpdateProcessor(
        private val requestsExecutor: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        val logger = LogManager.getLogger(LangCommandUpdateProcessor::class.java)!!
    }

    override suspend fun accept(update: Update, groupConfig: GroupConfig?, groupState: GroupState?): Boolean {
        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return false
        val message = update.data as? CommonMessageImpl<*> ?: return false
        val content = (message as? CommonMessageImpl<*>)?.content as? TextContent ?: return false

        return content
                .entities
                .any {
                    "lang" == (it.source as? BotCommandTextSource)?.command
                }
    }

    override suspend fun process(update: Update, groupConfig: GroupConfig?, groupState: GroupState?) {
        logger.debug("Initiating language change in {}", (update as MessageUpdate).data.chat.id.chatId)

        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return
        val message = update.data as? CommonMessageImpl<*> ?: return

        requestsExecutor.sendMessage(
                chatId = update.data.chat.id,
                text = I18N(groupConfig?.language)["language"],
                parseMode = MarkdownV2,
                replyMarkup = InlineKeyboardMarkup(
                        listOf(
                                listOf(
                                        CallbackDataInlineKeyboardButton("BY", "lang:by:${message.user.id.chatId}"),
                                        CallbackDataInlineKeyboardButton("EN", "lang:en:${message.user.id.chatId}"),
                                        CallbackDataInlineKeyboardButton("RU", "lang:ru:${message.user.id.chatId}"),
                                )
                        )
                )
        )
    }
}
