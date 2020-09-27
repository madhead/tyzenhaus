package me.madhead.tyzenhaus.repository.postgresql.group.state

import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.entity.group.state.GroupState
import me.madhead.tyzenhaus.repository.postgresql.PostgreSqlRepository
import org.apache.logging.log4j.LogManager
import javax.sql.DataSource

/**
 * PostgreSQL repository for [group configs][GroupConfig].
 */
class GroupStateRepository(dataSource: DataSource) : PostgreSqlRepository<Long, GroupState>(dataSource) {
    companion object {
        val logger = LogManager.getLogger(GroupStateRepository::class.java)!!
    }

    override fun save(entity: GroupState) {
        TODO()
    }

    override fun get(id: Long): GroupState? {
        TODO()
    }
}
