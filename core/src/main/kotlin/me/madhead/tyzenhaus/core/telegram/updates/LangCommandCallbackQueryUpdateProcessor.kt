package me.madhead.tyzenhaus.core.telegram.updates

import com.github.insanusmokrassar.TelegramBotAPI.bot.RequestsExecutor
import com.github.insanusmokrassar.TelegramBotAPI.extensions.api.answers.answerCallbackQuery
import com.github.insanusmokrassar.TelegramBotAPI.extensions.api.deleteMessage
import com.github.insanusmokrassar.TelegramBotAPI.extensions.api.send.sendMessage
import com.github.insanusmokrassar.TelegramBotAPI.types.CallbackQuery.MessageDataCallbackQuery
import com.github.insanusmokrassar.TelegramBotAPI.types.ParseMode.MarkdownV2
import com.github.insanusmokrassar.TelegramBotAPI.types.update.CallbackQueryUpdate
import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.Update
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.entity.group.state.GroupState
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.Repository
import org.apache.logging.log4j.LogManager
import java.util.Locale

/**
 * /lang command callback query handler.
 */
class LangCommandCallbackQueryUpdateProcessor(
        private val requestsExecutor: RequestsExecutor,
        private val groupConfigRepository: Repository<Long, GroupConfig>,
) : UpdateProcessor {
    companion object {
        val logger = LogManager.getLogger(LangCommandCallbackQueryUpdateProcessor::class.java)!!
    }

    override suspend fun accept(update: Update, groupConfig: GroupConfig?, groupState: GroupState?): Boolean {
        @Suppress("NAME_SHADOWING")
        val update = update as? CallbackQueryUpdate ?: return false
        val callbackQuery = update.data as? MessageDataCallbackQuery ?: return false

        return callbackQuery.data.startsWith("lang:")
    }

    override suspend fun process(update: Update, groupConfig: GroupConfig?, groupState: GroupState?) {
        @Suppress("NAME_SHADOWING")
        val update = update as? CallbackQueryUpdate ?: return
        val callbackQuery = update.data as? MessageDataCallbackQuery ?: return
        val groupId = callbackQuery.message.chat.id.chatId

        logger.debug("Changing language in {}", groupId)

        val (_, language, initiatingUserId) = callbackQuery.data.split(":")

        if (callbackQuery.user.id.chatId.toString() == initiatingUserId) {
            requestsExecutor.answerCallbackQuery(callbackQuery = callbackQuery)

            val newGroupConfig = (groupConfig ?: GroupConfig(groupId)).copy(language = Locale(language))

            groupConfigRepository.save(newGroupConfig)
            requestsExecutor.deleteMessage(callbackQuery.message)
            requestsExecutor.sendMessage(
                    chatId = callbackQuery.message.chat.id,
                    text = I18N(newGroupConfig.language)["language.response.ok"],
                    parseMode = MarkdownV2,
            )
        } else {
            requestsExecutor.answerCallbackQuery(
                    callbackQuery = callbackQuery,
                    text = I18N(groupConfig?.language)["language.response.wrongUser"],
            )
        }
    }
}
