package me.madhead.tyzenhaus.core.telegram.updates

import com.github.insanusmokrassar.TelegramBotAPI.bot.RequestsExecutor
import com.github.insanusmokrassar.TelegramBotAPI.extensions.api.send.sendMessage
import com.github.insanusmokrassar.TelegramBotAPI.types.MessageEntity.textsources.BotCommandTextSource
import com.github.insanusmokrassar.TelegramBotAPI.types.ParseMode.MarkdownV2
import com.github.insanusmokrassar.TelegramBotAPI.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import com.github.insanusmokrassar.TelegramBotAPI.types.buttons.InlineKeyboardMarkup
import com.github.insanusmokrassar.TelegramBotAPI.types.message.abstracts.CommonMessage
import com.github.insanusmokrassar.TelegramBotAPI.types.message.abstracts.FromUserMessage
import com.github.insanusmokrassar.TelegramBotAPI.types.message.content.TextContent
import com.github.insanusmokrassar.TelegramBotAPI.types.update.MessageUpdate
import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.Update
import me.madhead.tyzenhaus.entity.dialog.state.ChangingLanguage
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.DialogStateRepository
import org.apache.logging.log4j.LogManager

/**
 * /lang command handler.
 */
class LangCommandUpdateProcessor(
        private val requestsExecutor: RequestsExecutor,
        private val dialogStateRepository: DialogStateRepository,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(LangCommandUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return null
        val message = update.data as? CommonMessage<*> ?: return null
        val user = (message as? FromUserMessage)?.user ?: return null
        val content = (message as? CommonMessage<*>)?.content as? TextContent ?: return null

        return if (content.entities.any { "lang" == (it.source as? BotCommandTextSource)?.command }) {
            {
                logger.debug("Changing language in {}", update.data.chat.id.chatId)

                dialogStateRepository.save(ChangingLanguage(update.data.chat.id.chatId, user.id.chatId))

                requestsExecutor.sendMessage(
                        chatId = update.data.chat.id,
                        text = I18N(groupConfig?.language)["language.action.choose"],
                        parseMode = MarkdownV2,
                        replyMarkup = InlineKeyboardMarkup(
                                listOf(
                                        listOf(
                                                CallbackDataInlineKeyboardButton("BY", "lang:by"),
                                                CallbackDataInlineKeyboardButton("EN", "lang:en"),
                                                CallbackDataInlineKeyboardButton("RU", "lang:ru"),
                                        )
                                )
                        )
                )
            }
        } else null
    }
}
