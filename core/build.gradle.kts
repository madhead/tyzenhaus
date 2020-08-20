plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.13.3"))

    api("com.github.insanusmokrassar:TelegramBotAPI-all:0.27.11")
    api(project(":entity:group-state"))
    implementation(project(":i18n"))
    implementation("org.apache.logging.log4j:log4j-core")
}
