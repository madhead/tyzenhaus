import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    jacoco
    id("io.gitlab.arturbosch.detekt")
}

val libs = the<LibrariesForLibs>()

configure<DetektExtension> {
    parallel = true
    config.from(files("${rootProject.rootDir}/detekt.yml"))
    buildUponDefaultConfig = false
    source.from(files(projectDir))
}

configure<JacocoPluginExtension> {
    toolVersion = libs.versions.jacoco.get()
}

tasks {
    withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvm.get()))
        }
    }
    withType<Jar>().configureEach {
        // Workaround for https://stackoverflow.com/q/42174572/750510
        archiveBaseName.set(rootProject.name + "-" + project.path.removePrefix(":").replace(":", "-"))
    }
    withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            showStandardStreams = true
        }
    }
    withType<JacocoReport>().configureEach {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}
