package me.madhead.tyzenhaus.core.telegram.updates.expense

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.chat.members.getChatMember
import dev.inmo.tgbotapi.requests.chat.members.GetChatMember
import dev.inmo.tgbotapi.types.ChatIdentifier
import dev.inmo.tgbotapi.types.ChatMember.MemberChatMember
import dev.inmo.tgbotapi.types.CommonUser
import dev.inmo.tgbotapi.types.UserId

/**
 * Failsafe wrapper around [RequestsExecutor.getChatMember]. Used in situations where API might return an error (like user left the group).
 */
suspend fun RequestsExecutor.getChatMemberSafe(
    chatId: ChatIdentifier,
    userId: UserId
) = try {
    execute(GetChatMember(chatId, userId))
} catch (_: Exception) {
    MemberChatMember(
        CommonUser(
            id = userId,
            firstName = userId.chatId.toString(),
            lastName = userId.chatId.toString(),
            username = null,
        )
    )
}
