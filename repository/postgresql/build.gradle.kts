import java.net.URI
import org.gradle.api.plugins.jvm.JvmTestSuite

plugins {
    id("liquibase-convention")
}

dependencies {
    libs.bundles.boms.orNull?.forEach {
        api(platform(it))
    }

    api(projects.repository)
    api(projects.entity)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.log4j.api)

    liquibaseRuntime(libs.liquibase.core)
    liquibaseRuntime(libs.picocli)
    liquibaseRuntime(libs.postgresql)
}

testing {
    suites {
        named<JvmTestSuite>("dbTest") {
            dependencies {
                implementation(libs.postgresql)
                runtimeOnly(libs.log4j.core)
            }
        }
    }
}

liquibase {
    activities {
        register("tyzenhaus") {
            val databaseUri = URI(System.getenv("DATABASE_URL")!!)

            this.arguments = mapOf(
                "url" to "jdbc:postgresql://${databaseUri.host}:${databaseUri.port}${databaseUri.path}",
                "username" to databaseUri.userInfo.split(":")[0],
                "password" to databaseUri.userInfo.split(":")[1],
                "driver" to "org.postgresql.Driver",
                "searchPath" to project.projectDir,
                "changelogFile" to "src/main/liquibase/changelog.yml"
            )
        }
    }
}
