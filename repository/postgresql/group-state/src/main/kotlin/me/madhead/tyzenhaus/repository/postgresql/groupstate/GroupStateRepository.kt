package me.madhead.tyzenhaus.repository.postgresql.groupstate

import me.madhead.tyzenhaus.entity.groupstate.GroupState
import me.madhead.tyzenhaus.repository.Repository
import org.apache.logging.log4j.LogManager
import javax.sql.DataSource

/**
 * PostgreSQL repository for [group states][GroupState].
 */
class GroupStateRepository(private val dataSource: DataSource) : Repository<Long, GroupState> {
    companion object {
        val logger = LogManager.getLogger(GroupStateRepository::class.java)!!
    }

    override fun get(id: Long): GroupState? = null

    override fun save(entity: GroupState) {
        TODO("Not yet implemented")
    }
}
