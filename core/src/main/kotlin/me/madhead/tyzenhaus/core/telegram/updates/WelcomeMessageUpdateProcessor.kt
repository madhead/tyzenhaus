package me.madhead.tyzenhaus.core.telegram.updates

import com.github.insanusmokrassar.TelegramBotAPI.bot.RequestsExecutor
import com.github.insanusmokrassar.TelegramBotAPI.extensions.api.send.sendMessage
import com.github.insanusmokrassar.TelegramBotAPI.types.ChatIdentifier
import com.github.insanusmokrassar.TelegramBotAPI.types.ParseMode.MarkdownV2
import com.github.insanusmokrassar.TelegramBotAPI.types.message.ChatEvents.NewChatMembers
import com.github.insanusmokrassar.TelegramBotAPI.types.message.abstracts.ChatEventMessage
import com.github.insanusmokrassar.TelegramBotAPI.types.update.MessageUpdate
import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.Update
import me.madhead.tyzenhaus.entity.groupstate.GroupState
import me.madhead.tyzenhaus.i18.I18N

class WelcomeMessageUpdateProcessor(
        private val id: ChatIdentifier,
        private val requestsExecutor: RequestsExecutor,
) : UpdateProcessor {
    override suspend fun accepts(update: Update, groupState: GroupState?): Boolean {
        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return false
        val message = update.data as? ChatEventMessage ?: return false
        val event = message.chatEvent as? NewChatMembers ?: return false

        return event.members.any { it.id == id }
    }

    override suspend fun process(update: Update, groupState: GroupState?) {
        requestsExecutor.sendMessage(
                chatId = (update as MessageUpdate).data.chat.id,
                text = I18N.messages().welcome(),
                parseMode = MarkdownV2
        )
    }
}
