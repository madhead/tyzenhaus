package me.madhead.tyzenhaus.repository.postgresql

import java.io.File
import javax.sql.DataSource
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.DirectoryResourceAccessor
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.postgresql.ds.PGSimpleDataSource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

/**
 * Base class for the PostgreSQL repository tests.
 *
 * Every test class gets its own disposable [PostgreSQLContainer] with the schema applied from the Liquibase
 * changelog and the test data loaded from `seed.sql`. This makes the tests fully independent and self-contained:
 * no external database, migrations or seeding are required to run them.
 */
@Suppress("UnnecessaryAbstractClass")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractRepositoryTest {
    private val postgres = PostgreSQLContainer(DockerImageName.parse("postgres:14"))

    protected lateinit var dataSource: DataSource
        private set

    @BeforeAll
    fun setUpDatabase() {
        postgres.start()

        dataSource = PGSimpleDataSource().apply {
            setUrl(postgres.jdbcUrl)
            user = postgres.username
            password = postgres.password
        }

        migrate()
        seed()
    }

    @AfterAll
    fun tearDownDatabase() {
        postgres.stop()
    }

    private fun migrate() {
        dataSource.connection.use { connection ->
            val database = DatabaseFactory
                .getInstance()
                .findCorrectDatabaseImplementation(JdbcConnection(connection))

            DirectoryResourceAccessor(File("src/main")).use { resourceAccessor ->
                Liquibase("liquibase/changelog.yml", resourceAccessor, database).use { liquibase ->
                    liquibase.update(Contexts(), LabelExpression())
                }
            }
        }
    }

    private fun seed() {
        val seed = javaClass.getResource("/seed.sql")!!.readText()

        dataSource.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.execute(seed)
            }
        }
    }
}
