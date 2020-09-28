package me.madhead.tyzenhaus.core.telegram.updates

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.deleteMessage
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.CallbackQuery.MessageDataCallbackQuery
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import me.madhead.tyzenhaus.entity.dialog.state.ChangingLanguage
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.DialogStateRepository
import me.madhead.tyzenhaus.repository.GroupConfigRepository
import org.apache.logging.log4j.LogManager
import java.util.Locale

/**
 * Language change callback handler.
 */
class LangCallbackQueryUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
    private val dialogStateRepository: DialogStateRepository,
    private val groupConfigRepository: GroupConfigRepository,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(LangCallbackQueryUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? CallbackQueryUpdate ?: return null
        val callbackQuery = update.data as? MessageDataCallbackQuery ?: return null

        return if (callbackQuery.data.startsWith("lang:") && (dialogState is ChangingLanguage)) {
            {
                val (_, language) = callbackQuery.data.split(":")

                logger.debug("{} changed language in {} to {}", update.userId, update.groupId, language)

                val newGroupConfig = (groupConfig
                    ?: GroupConfig(callbackQuery.message.chat.id.chatId)).copy(language = Locale(language))

                groupConfigRepository.save(newGroupConfig)
                requestsExecutor.answerCallbackQuery(callbackQuery = callbackQuery)
                requestsExecutor.deleteMessage(callbackQuery.message)
                requestsExecutor.sendMessage(
                    chatId = callbackQuery.message.chat.id,
                    text = I18N(newGroupConfig.language)["language.response.ok"],
                    parseMode = MarkdownV2,
                )
                dialogStateRepository.delete(callbackQuery.message.chat.id.chatId, callbackQuery.user.id.chatId)
            }
        } else if (callbackQuery.data.startsWith("lang:") && (dialogState !is ChangingLanguage)) {
            {
                requestsExecutor.answerCallbackQuery(
                    callbackQuery = callbackQuery,
                    text = I18N(groupConfig?.language)["language.response.wrongUser"],
                )
            }
        } else null
    }
}
