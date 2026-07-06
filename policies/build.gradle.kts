import org.asciidoctor.gradle.jvm.AsciidoctorTask

plugins {
    id("asciidoctor-convention")
}

tasks {
    register<AsciidoctorTask>("privacy") {
        description = "Build privacy policy"
        setSourceDir(file("src/main/asciidoc"))
        sources {
            include("privacy.adoc")
        }
        setOutputDir(layout.buildDirectory.asFile)
        setBaseDir(file("src/main/asciidoc"))
        attributes(mapOf(
            "revdate" to shellout(
                "git", "log", "-1", "--pretty=format:%cd", "--date=format:%b %d, %Y", file("src/main/asciidoc/privacy.adoc").absolutePath
            ),
            "revnumber" to shellout(
                "git", "log", "-1", "--pretty=format:%h", file("src/main/asciidoc/privacy.adoc").absolutePath
            ),
            "version-label" to "Revision:",
        ))
    }
}

private fun shellout(vararg command: String): String =
    providers.exec {
        commandLine(*command)
    }.standardOutput.asText.get().trim()
