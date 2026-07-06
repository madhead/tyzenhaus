import io.gitlab.arturbosch.detekt.report.ReportMergeTask

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(libs.liquibase.core)
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${libs.versions.plugin.detekt.get()}")
    }
}

tasks.register<ReportMergeTask>("detektReportMerge") {
    description = "Merge individual SARIF files into one"
    output.set(layout.buildDirectory.file("reports/detekt/merge.sarif"))
}
