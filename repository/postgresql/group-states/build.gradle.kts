plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.13.3"))

    implementation("org.apache.logging.log4j:log4j-core")
    api(project(":entity:group-state"))
}
