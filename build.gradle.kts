import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm").version("1.4.0").apply(false)
}

allprojects {
    repositories {
        jcenter()
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "13"
        }
        withType<Jar> {
            // Workaround for https://stackoverflow.com/q/42174572/750510
            archiveBaseName.set(rootProject.name + "-" + this.project.path.removePrefix(":").replace(":", "-"))
        }
    }
}
