import java.net.URI

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

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.postgresql)

    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.log4j.core)

    liquibaseRuntime(libs.liquibase.core)
    liquibaseRuntime(libs.picocli)
    liquibaseRuntime(libs.postgresql)
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
