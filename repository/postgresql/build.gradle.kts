import java.net.URI

plugins {
    kotlin("jvm")
    id("org.liquibase.gradle")
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

tasks {
    test {
        useJUnitPlatform {
            excludeTags("db")
        }
    }

    val dbTest by registering(Test::class) {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Runs the DB tests."
        shouldRunAfter("test")
        outputs.upToDateWhen { false }
        useJUnitPlatform {
            includeTags("db")
        }
    }

    val jacocoDbTestReport by registering(JacocoReport::class) {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Generates code coverage report for the dbTest task."
        executionData(dbTest.get())
        sourceSets(sourceSets.main.orNull)

        val reportsDirectory = project.extensions.getByType<JacocoPluginExtension>().reportsDirectory.get().asFile

        reports.all {
            when (val outputLocation = this.outputLocation) {
                is DirectoryProperty -> {
                    outputLocation.set(File(reportsDirectory, "dbTest" + "/" + this.name))
                }

                is RegularFileProperty -> {
                    outputLocation.set(File(reportsDirectory, "dbTest" + "/" + this@registering.name + "." + this.name))
                }
            }
        }
    }
}
