import java.io.ByteArrayOutputStream
import org.asciidoctor.gradle.jvm.AsciidoctorTask

plugins {
    alias(libs.plugins.asciidoctor)
}

tasks {
    register<AsciidoctorTask>("privacy") {
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
    ByteArrayOutputStream().use { stdout ->
        exec {
            commandLine(*command)
            standardOutput = stdout
        }

        stdout.toString()
    }
