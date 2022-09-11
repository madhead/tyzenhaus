package me.madhead.tyzenhaus.core.telegram.updates.expense

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.chat.members.getChatMember
import dev.inmo.tgbotapi.types.ChatIdentifier
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.chat.CommonUser
import dev.inmo.tgbotapi.types.chat.member.MemberChatMemberImpl

/**
 * Failsafe wrapper around [RequestsExecutor.getChatMember]. Used in situations where API might return an error (like user left the group).
 */
suspend fun RequestsExecutor.getChatMemberSafe(
    chatId: ChatIdentifier,
    userId: UserId
) = try {
    getChatMember(chatId, userId)
} catch (_: Exception) {
    MemberChatMemberImpl(
        CommonUser(
            id = userId,
            firstName = userId.chatId.toString(),
            lastName = userId.chatId.toString(),
            username = null,
        )
    )
}
