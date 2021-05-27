package me.madhead.tyzenhaus.core.telegram.updates

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.MessageEntity.textsources.BotCommandTextSource
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
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
        val content = (message as? CommonMessage<*>)?.content as? TextContent ?: return null

        return if (content.textSources.any { "lang" == (it as? BotCommandTextSource)?.command }) {
            {
                logger.debug("{} initiated a language change in {}", update.userId, update.groupId)

                dialogStateRepository.save(ChangingLanguage(update.groupId, update.userId))
                requestsExecutor.sendMessage(
                    chatId = update.data.chat.id,
                    text = I18N(groupConfig?.language)["language.action.choose"],
                    parseMode = MarkdownV2,
                    replyMarkup = InlineKeyboardMarkup(
                        listOf(
                            listOf(
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
