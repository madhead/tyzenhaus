import org.gradle.api.plugins.jvm.JvmTestSuite

plugins {
    id("kotlin-convention")
}

testing {
    suites {
        register<JvmTestSuite>("dbTest") {
            dependencies {
                implementation(project())
            }

            targets.configureEach {
                testTask.configure {
                    description = "Runs the DB tests."
                    shouldRunAfter("test")
                    outputs.upToDateWhen { false }
                }
            }
        }
    }
}

tasks {
    register<JacocoReport>("jacocoDbTestReport") {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Generates code coverage report for the dbTest task."
        dependsOn(named<Test>("dbTest"))
        executionData(named<Test>("dbTest").get())
        sourceSets(sourceSets.findByName("main"))

        val reportsDirectory = project.extensions.getByType<JacocoPluginExtension>().reportsDirectory.get().asFile

        reports.all {
            when (val outputLocation = this.outputLocation) {
                is DirectoryProperty -> {
                    outputLocation.set(File(reportsDirectory, "dbTest/" + this.name))
                }
                is RegularFileProperty -> {
                    outputLocation.set(File(reportsDirectory, "dbTest/" + this@register.name + "." + this.name))
                }
            }
        }
    }
}
