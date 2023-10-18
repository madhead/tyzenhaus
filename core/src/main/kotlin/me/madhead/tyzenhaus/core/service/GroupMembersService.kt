package me.madhead.tyzenhaus.core.service

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.UserId
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
        val chatMembers = groupConfig.members.map { requestsExecutor.getChatMemberSafe(ChatId(groupConfig.id), UserId(it)) }

        return GroupMembers(
            id = groupConfig.id,
            members = chatMembers.map {
                GroupMember(
                    id = it.user.id.chatId,
                    firstName = it.user.firstName,
                    lastName = it.user.lastName,
                )
            }
        )
    }
}
