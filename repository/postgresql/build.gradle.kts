import java.net.URI

plugins {
    kotlin("jvm")
    id("org.liquibase.gradle")
}

dependencies {
    implementation(platform("org.apache.logging.log4j:log4j-bom:${Versions.Dependencies.LOG4J}"))

    api(project(":repository"))
    api(project(":entity"))

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

    val dbTest by creating(Test::class) {
        shouldRunAfter("test")
        useJUnitPlatform {
            includeTags("db")
        }
    }
}
