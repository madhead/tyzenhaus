import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm").version(Versions.Plugins.KOTLIN).apply(false)
    kotlin("plugin.serialization").version(Versions.Plugins.KOTLIN).apply(false)
    id("org.liquibase.gradle").version(Versions.Plugins.LIQUIBASE).apply(false)
    id("io.gitlab.arturbosch.detekt").version(Versions.Plugins.DETEKT).apply(false)
}

allprojects {
    apply<DetektPlugin>()
    apply<JacocoPlugin>()

    configure<DetektExtension> {
        parallel = true
        config.from(files("$rootDir/detekt.yml"))
        buildUponDefaultConfig = false
        source.from(files(projectDir))
    }

    configure<JacocoPluginExtension> {
        toolVersion = Versions.Plugins.JACOCO
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = Versions.JVM
        }
        withType<Jar> {
            // Workaround for https://stackoverflow.com/q/42174572/750510
            archiveBaseName.set(rootProject.name + "-" + this.project.path.removePrefix(":").replace(":", "-"))
        }
        withType<Test> {
            useJUnitPlatform()
            testLogging {
                showStandardStreams = true
            }
        }
        withType<JacocoReport> {
            reports {
                xml.required.set(true)
                html.required.set(true)
            }
        }
    }
}
