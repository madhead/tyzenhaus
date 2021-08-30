package me.madhead.tyzenhaus.core.telegram.updates.expense

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.chat.members.getChatMember
import dev.inmo.tgbotapi.extensions.api.edit.ReplyMarkup.editMessageReplyMarkup
import dev.inmo.tgbotapi.types.CallbackQuery.MessageDataCallbackQuery
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.core.telegram.updates.groupId
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForParticipants
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.DialogStateRepository
import org.apache.logging.log4j.LogManager

/**
 * Check / uncheck a participant.
 */
class ParticipantCallbackQueryUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
    private val dialogStateRepository: DialogStateRepository,
) : UpdateProcessor {
    companion object {
        const val CALLBACK_PREFIX = "participant:"

        private val logger = LogManager.getLogger(ParticipantCallbackQueryUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? CallbackQueryUpdate ?: return null
        val callbackQuery = update.data as? MessageDataCallbackQuery ?: return null
        val members = groupConfig?.members ?: return null

        return if (callbackQuery.data.startsWith(CALLBACK_PREFIX) && (dialogState is WaitingForParticipants)) {
            {
                val (_, rawParticipant) = callbackQuery.data.split(":")

                logger.debug("Toggling participation status for {}", rawParticipant)

                val participant = rawParticipant.toLongOrNull()

                if (participant != null) {
                    val chatMembers = members.map { requestsExecutor.getChatMember(ChatId(update.groupId), UserId(it)) }
                    val state = dialogState.copy(
                        participants = if (dialogState.participants.contains(participant)) {
                            dialogState.participants - participant
                        } else {
                            dialogState.participants + participant
                        }
                    )

                    dialogStateRepository.save(state)
                    requestsExecutor.answerCallbackQuery(callbackQuery = callbackQuery)
                    requestsExecutor.editMessageReplyMarkup(
                        message = callbackQuery.message,
                        replyMarkup = replyMarkup(chatMembers, state.participants, groupConfig)
                    )
                } else {
                    logger.warn("Invalid participant: {}", rawParticipant)
                }
            }
        } else if (callbackQuery.data.startsWith(CALLBACK_PREFIX) && (dialogState !is WaitingForParticipants)) {
            {
                requestsExecutor.answerCallbackQuery(
                    callbackQuery = callbackQuery,
                    text = I18N(groupConfig.language)["expense.response.participants.wrongState"],
                )
            }
        } else null
    }
}
