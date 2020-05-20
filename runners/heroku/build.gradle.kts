import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(platform("io.ktor:ktor-bom:1.3.1"))
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.13.2"))

    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-server-netty")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl")
}

application {
    applicationName = "tyzenhaus"
    mainClassName = "io.ktor.server.netty.EngineMain"
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "13"
    }
}
