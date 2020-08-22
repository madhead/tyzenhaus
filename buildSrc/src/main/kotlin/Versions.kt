/**
 * Global version constants.
 */
object Versions {
    /**
     * Target JVM.
     */
    const val JVM = "13"

    /**
     * Gradle plugins versions.
     */
    object Plugins {
        const val KOTLIN = "1.4.0"
        const val LIQUIBASE = "2.0.4"
        const val DETEKT = "1.11.2"
    }

    /**
     * Dependencies versions.
     */
    object Dependencies {
        const val KTOR = "1.4.0"
        const val KOIN = "2.1.6"
        const val TELEGRAM_BOT_API = "0.27.11"
        const val POSTGRESQL = "42.2.16"
        const val LOG4J = "2.13.3"
        const val C10N = "1.3"
        const val JUNIT = "5.6.2"
        const val LIQUIBASE = "3.10.2"
        const val SNAKEYAML = "1.26"
    }
}
