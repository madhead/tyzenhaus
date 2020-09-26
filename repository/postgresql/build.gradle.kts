import java.net.URI

plugins {
    kotlin("jvm")
    id("org.liquibase.gradle")
}

dependencies {
    implementation(platform("org.apache.logging.log4j:log4j-bom:${Versions.Dependencies.LOG4J}"))

    api(project(":repository"))
    api(project(":entity"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.Dependencies.KOTLINX_SERIALIZATION}")
    implementation("org.apache.logging.log4j:log4j-core")

    testImplementation(platform("org.junit:junit-bom:${Versions.Dependencies.JUNIT}"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.postgresql:postgresql:${Versions.Dependencies.POSTGRESQL}")

    testRuntimeOnly(platform("org.junit:junit-bom:${Versions.Dependencies.JUNIT}"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    liquibaseRuntime("org.liquibase:liquibase-core:${Versions.Dependencies.LIQUIBASE}")
    liquibaseRuntime("org.yaml:snakeyaml:${Versions.Dependencies.SNAKEYAML}")
    liquibaseRuntime("org.postgresql:postgresql:${Versions.Dependencies.POSTGRESQL}")
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
                    "changeLogFile" to file("src/main/liquibase/changelog.yml")
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

        val reportsDir = project.extensions.getByType<JacocoPluginExtension>().reportsDir

        reports.all {
            if (this.outputType == Report.OutputType.DIRECTORY) {
                this.destination = File(reportsDir, "dbTest" + "/" + this.name)
            } else {
                this.destination = File(reportsDir, "dbTest" + "/" + this@registering.name + "." + this.name)
            }
        }
    }
}
