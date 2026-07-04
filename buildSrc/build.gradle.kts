plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.kotlin:kotlin-serialization:${libs.versions.kotlin.get()}")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${libs.versions.plugin.detekt.get()}")
    implementation("org.liquibase:liquibase-gradle-plugin:${libs.versions.plugin.liquibase.get()}")
    implementation("org.asciidoctor:asciidoctor-gradle-jvm:${libs.versions.plugin.asciidoctor.get()}")
    implementation("org.liquibase:liquibase-core:${libs.versions.liquibase.get()}")
}
