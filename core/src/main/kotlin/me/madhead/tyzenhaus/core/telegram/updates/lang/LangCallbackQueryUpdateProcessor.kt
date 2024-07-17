package me.madhead.tyzenhaus.core.telegram.updates.lang

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.edit.text.editMessageText
import dev.inmo.tgbotapi.types.message.MarkdownV2
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import java.util.Locale
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.core.telegram.updates.groupId
import me.madhead.tyzenhaus.core.telegram.updates.userId
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.GroupConfigRepository
import org.apache.logging.log4j.LogManager

/**
 * Language change callback handler.
 */
class LangCallbackQueryUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
    private val groupConfigRepository: GroupConfigRepository,
) : UpdateProcessor {
    companion object {
        const val CALLBACK_PREFIX = "lang:"

        private val logger = LogManager.getLogger(LangCallbackQueryUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? CallbackQueryUpdate ?: return null
        val callbackQuery = update.data as? MessageDataCallbackQuery ?: return null

        if (!callbackQuery.data.startsWith(CALLBACK_PREFIX)) return null

        return {
            val (_, language) = callbackQuery.data.split(":")

            logger.debug("{} changed language in {} to {}", update.userId, update.groupId, language)

            val newGroupConfig = (groupConfig
                ?: GroupConfig(callbackQuery.message.chat.id.chatId.long)).copy(language = Locale.of(language))

            CoroutineScope(coroutineContext + Dispatchers.IO).launch {
                groupConfigRepository.save(newGroupConfig)
            }

            requestsExecutor.answerCallbackQuery(callbackQuery = callbackQuery)
            requestsExecutor.editMessageText(
                chat = callbackQuery.message.chat,
                messageId = callbackQuery.message.messageId,
                text = I18N(newGroupConfig.language)["language.response.ok"],
                parseMode = MarkdownV2,
                replyMarkup = null,
            )
        }
    }
}
