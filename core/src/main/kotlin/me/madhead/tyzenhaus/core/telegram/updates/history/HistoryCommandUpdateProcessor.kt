package me.madhead.tyzenhaus.core.telegram.updates.history

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.chat.ExtendedBot
import dev.inmo.tgbotapi.types.message.MarkdownV2
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.textsources.BotCommandTextSource
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import java.time.Duration
import java.time.Instant
import java.util.UUID
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.core.telegram.updates.groupId
import me.madhead.tyzenhaus.core.telegram.updates.userId
import me.madhead.tyzenhaus.entity.api.token.APIToken
import me.madhead.tyzenhaus.entity.api.token.Scope
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.APITokenRepository
import org.apache.logging.log4j.LogManager

/**
 * /history command handler.
 */
class HistoryCommandUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
    private val me: ExtendedBot,
    private val apiTokenRepository: APITokenRepository,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(HistoryCommandUpdateProcessor::class.java)!!
    }

    @Suppress("DuplicatedCode")
    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return null
        val message = update.data as? CommonMessage<*> ?: return null
        val content = (message as? CommonMessage<*>)?.content as? TextContent ?: return null

        return if (content.textSources.any { "history" == (it as? BotCommandTextSource)?.command }) {
            {
                logger.debug("{} asked for history in {}", update.userId, update.groupId)

                val token = APIToken(
                    token = UUID.randomUUID(),
                    groupId = update.groupId,
                    scope = Scope.HISTORY,
                    validUntil = Instant.now() + @Suppress("MagicNumber") Duration.ofMinutes(30)
                )

                apiTokenRepository.save(token)

                requestsExecutor.sendMessage(
                    chatId = update.data.chat.id,
                    text = I18N(groupConfig?.language)["history.response", me.username.usernameWithoutAt, token.token.toString()],
                    parseMode = MarkdownV2,
                )
            }
        } else null
    }
}
