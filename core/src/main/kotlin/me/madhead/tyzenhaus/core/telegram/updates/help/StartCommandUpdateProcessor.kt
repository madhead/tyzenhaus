package me.madhead.tyzenhaus.core.telegram.updates.help

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.LinkPreviewOptions.Disabled
import dev.inmo.tgbotapi.types.chat.CommonUser
import dev.inmo.tgbotapi.types.message.MarkdownV2
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.abstracts.PrivateContentMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.textsources.BotCommandTextSource
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import java.util.Locale
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.core.telegram.updates.groupId
import me.madhead.tyzenhaus.core.telegram.updates.userId
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import org.apache.logging.log4j.LogManager

/**
 * /start command handler.
 */
class StartCommandUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(StartCommandUpdateProcessor::class.java)!!
    }

    @Suppress("DuplicatedCode")
    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return null
        val message = update.data as? CommonMessage<*> ?: return null
        val content = (message as? CommonMessage<*>)?.content as? TextContent ?: return null

        return if (content.textSources.any { "start" == (it as? BotCommandTextSource)?.command }) {
            {
                logger.debug("{} started bit in {}", update.userId, update.groupId)

                if (message is PrivateContentMessage<*>) {
                    requestsExecutor.sendMessage(
                        chatId = update.data.chat.id,
                        text = I18N(groupConfig?.language ?: (message.user as? CommonUser)?.languageCode?.let { Locale.of(it) })["start"],
                        parseMode = MarkdownV2,
                        linkPreviewOptions = Disabled,
                    )
                } else {
                    requestsExecutor.sendMessage(
                        chatId = update.data.chat.id,
                        text = I18N(groupConfig?.language)["help"],
                        parseMode = MarkdownV2,
                        linkPreviewOptions = Disabled,
                    )
                }
            }
        } else null
    }
}
