package me.madhead.tyzenhaus.entity.group.members

import kotlinx.serialization.Serializable

/**
 *  Information about a member of a group.
 */
@Serializable
data class GroupMember(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val username: String?,
)

/**
 * Information about group members.
 */
@Serializable
data class GroupMembers(
    val id: Long,
    val members: Collection<GroupMember> = emptySet(),
)
