plugins {
    id("kotlin-convention")
}

tasks {
    named<Test>("test") {
        useJUnitPlatform {
            excludeTags("db")
        }
    }

    val dbTest = register<Test>("dbTest") {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Runs the DB tests."
        shouldRunAfter("test")
        outputs.upToDateWhen { false }
        useJUnitPlatform {
            includeTags("db")
        }
    }

    register<JacocoReport>("jacocoDbTestReport") {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Generates code coverage report for the dbTest task."
        executionData(dbTest.get())
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
