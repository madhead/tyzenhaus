import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import java.net.URI
import java.util.Properties

plugins {
    kotlin("jvm")
    id("org.liquibase.gradle")
}

dependencies {
    api(project(":repository"))

    liquibaseRuntime("org.liquibase:liquibase-core:${Versions.Dependencies.LIQUIBASE}")
    liquibaseRuntime("org.yaml:snakeyaml:${Versions.Dependencies.SNAKEYAML}")
    liquibaseRuntime("org.postgresql:postgresql:${Versions.Dependencies.POSTGRESQL}")
}

liquibase {
    activities {
        register("tyzenhaus") {
            val env = Properties().apply {
                load(rootProject.file(".env").bufferedReader())
            }
            val databaseUri = URI(env["DATABASE_URL"].toString())

            this.arguments = mapOf(
                    "url" to "jdbc:postgresql://${databaseUri.host}:${databaseUri.port}${databaseUri.path}",
                    "username" to databaseUri.userInfo.split(":")[0],
                    "password" to databaseUri.userInfo.split(":")[1],
                    "driver" to "org.postgresql.Driver",
                    "changeLogFile" to file("src/main/liquibase/changelog.yml").absolutePath
            )
        }
    }
}

subprojects {
    apply<KotlinPluginWrapper>()

    sourceSets {
        create("dbTest") {
            compileClasspath += sourceSets.main.get().output
            runtimeClasspath += sourceSets.main.get().output
        }
    }

    val dbTestImplementation: Configuration by configurations.getting {
        extendsFrom(configurations.testImplementation.get())
    }
    val dbTestRuntimeOnly: Configuration by configurations.getting {
        extendsFrom(configurations.testRuntimeOnly.get())
    }

    dependencies {
        implementation(platform("org.apache.logging.log4j:log4j-bom:${Versions.Dependencies.LOG4J}"))

        api(project(":repository:postgresql"))
        implementation("org.apache.logging.log4j:log4j-core")

        testImplementation(platform("org.junit:junit-bom:${Versions.Dependencies.JUNIT}"))
        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testImplementation("org.junit.jupiter:junit-jupiter-params")

        testRuntimeOnly(platform("org.junit:junit-bom:${Versions.Dependencies.JUNIT}"))
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

        dbTestImplementation("org.postgresql:postgresql:${Versions.Dependencies.POSTGRESQL}")
    }

    tasks {
        @Suppress("UNUSED_VARIABLE")
        val dbTest by creating(Test::class) {
            val dbTest by sourceSets

            group = "verification"
            testClassesDirs = dbTest.output.classesDirs
            classpath = dbTest.runtimeClasspath
            shouldRunAfter("test")
        }
    }
}
