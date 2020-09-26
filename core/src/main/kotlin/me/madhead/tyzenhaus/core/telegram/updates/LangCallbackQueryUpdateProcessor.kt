package me.madhead.tyzenhaus.core.telegram.updates

import com.github.insanusmokrassar.TelegramBotAPI.bot.RequestsExecutor
import com.github.insanusmokrassar.TelegramBotAPI.extensions.api.answers.answerCallbackQuery
import com.github.insanusmokrassar.TelegramBotAPI.extensions.api.deleteMessage
import com.github.insanusmokrassar.TelegramBotAPI.extensions.api.send.sendMessage
import com.github.insanusmokrassar.TelegramBotAPI.types.CallbackQuery.MessageDataCallbackQuery
import com.github.insanusmokrassar.TelegramBotAPI.types.ParseMode.MarkdownV2
import com.github.insanusmokrassar.TelegramBotAPI.types.update.CallbackQueryUpdate
import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.Update
import me.madhead.tyzenhaus.entity.dialog.state.ChangingLanguage
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.DialogStateRepository
import me.madhead.tyzenhaus.repository.GroupConfigRepository
import org.apache.logging.log4j.LogManager
import java.util.Locale

/**
 * /lang command handler.
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

                logger.debug("Changing language in {} to {}", callbackQuery.message.chat.id.chatId, language)

                val newGroupConfig = (groupConfig ?: GroupConfig(callbackQuery.message.chat.id.chatId)).copy(language = Locale(language))

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
