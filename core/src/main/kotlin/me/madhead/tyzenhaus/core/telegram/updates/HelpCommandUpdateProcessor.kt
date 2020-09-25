package me.madhead.tyzenhaus.core.telegram.updates

import com.github.insanusmokrassar.TelegramBotAPI.bot.RequestsExecutor
import com.github.insanusmokrassar.TelegramBotAPI.extensions.api.send.sendMessage
import com.github.insanusmokrassar.TelegramBotAPI.types.MessageEntity.textsources.BotCommandTextSource
import com.github.insanusmokrassar.TelegramBotAPI.types.ParseMode.MarkdownV2
import com.github.insanusmokrassar.TelegramBotAPI.types.message.abstracts.CommonMessage
import com.github.insanusmokrassar.TelegramBotAPI.types.message.content.TextContent
import com.github.insanusmokrassar.TelegramBotAPI.types.update.MessageUpdate
import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.Update
import me.madhead.tyzenhaus.entity.groupconfig.GroupConfig
import me.madhead.tyzenhaus.entity.groupstate.GroupState
import me.madhead.tyzenhaus.i18.I18N
import org.apache.logging.log4j.LogManager

/**
 * /help command handler.
 */
class HelpCommandUpdateProcessor(
        private val requestsExecutor: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        val logger = LogManager.getLogger(HelpCommandUpdateProcessor::class.java)!!
    }

    override suspend fun accept(update: Update, groupConfig: GroupConfig?, groupState: GroupState?): Boolean {
        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return false
        val message = update.data as? CommonMessage<*> ?: return false
        val content = (message as? CommonMessage<*>)?.content as? TextContent ?: return false

        return content
                .entities
                .any {
                    "help" == (it.source as? BotCommandTextSource)?.command
                }
    }

    override suspend fun process(update: Update, groupConfig: GroupConfig?, groupState: GroupState?) {
        logger.debug("Helping in {}", (update as MessageUpdate).data.chat.id.chatId)

        requestsExecutor.sendMessage(
                chatId = update.data.chat.id,
                text = I18N(groupConfig?.language)["help"],
                parseMode = MarkdownV2
        )
    }
}
