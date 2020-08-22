plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform("org.apache.logging.log4j:log4j-bom:${Versions.Dependencies.LOG4J}"))

    api("com.github.insanusmokrassar:TelegramBotAPI-all:${Versions.Dependencies.TELEGRAM_BOT_API}")
    api(project(":entity:group-config"))
    api(project(":entity:group-state"))
    api(project(":repository"))
    implementation(project(":i18n"))
    implementation("org.apache.logging.log4j:log4j-core")
}
