package me.madhead.tyzenhaus.core.service

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.toChatId
import me.madhead.tyzenhaus.core.telegram.updates.expense.getChatMemberSafe
import me.madhead.tyzenhaus.entity.group.members.GroupMember
import me.madhead.tyzenhaus.entity.group.members.GroupMembers
import me.madhead.tyzenhaus.repository.GroupConfigRepository

/**
 * Lists group members.
 */
class GroupMembersService(
    private val groupConfigRepository: GroupConfigRepository,
    private val requestsExecutor: RequestsExecutor,
) {
    /**
     * Lists members of the [group].
     */
    suspend fun groupMembers(group: Long): GroupMembers? {
        val groupConfig = groupConfigRepository.get(group) ?: return null
        val chatMembers = groupConfig.members.map { requestsExecutor.getChatMemberSafe(groupConfig.id.toChatId(), it.toChatId()) }

        return GroupMembers(
            id = groupConfig.id,
            members = chatMembers.map {
                GroupMember(
                    id = it.user.id.chatId.long,
                    firstName = it.user.firstName,
                    lastName = it.user.lastName,
                    username = it.user.username?.withoutAt
                )
            }
        )
    }
}
