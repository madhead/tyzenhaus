plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform("org.apache.logging.log4j:log4j-bom:${Versions.Dependencies.LOG4J}"))

    api("com.github.insanusmokrassar:TelegramBotAPI:${Versions.Dependencies.TELEGRAM_BOT_API}")
    api(project(":entity"))
    api(project(":repository"))
    implementation(project(":i18n"))
    implementation("org.apache.logging.log4j:log4j-core")
}
