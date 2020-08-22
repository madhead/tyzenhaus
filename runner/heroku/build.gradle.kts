plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(platform("io.ktor:ktor-bom:${Versions.Dependencies.KTOR}"))
    implementation(platform("org.apache.logging.log4j:log4j-bom:${Versions.Dependencies.LOG4J}"))

    implementation("io.ktor:ktor-server-netty")
    implementation("org.koin:koin-ktor:${Versions.Dependencies.KOIN}")
    implementation("com.github.insanusmokrassar:TelegramBotAPI-all:${Versions.Dependencies.TELEGRAM_BOT_API}")
    implementation("org.postgresql:postgresql:${Versions.Dependencies.POSTGRESQL}")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl")
    implementation(project(":repository:postgresql:group-config"))
    implementation(project(":repository:postgresql:group-state"))
    implementation(project(":core"))
}

application {
    applicationName = "tyzenhaus"
    mainClassName = "io.ktor.server.netty.EngineMain"
}
