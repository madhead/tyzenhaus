package me.madhead.tyzenhaus.launcher.fly

import io.ktor.server.config.ConfigLoader
import io.ktor.server.config.ConfigLoader.Companion.load
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.applicationEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.util.concurrent.TimeUnit
import me.madhead.tyzenhaus.launcher.fly.modules.tyzenhaus

/**
 * [Ktor-based](https://ktor.io) Tyzenhaus launcher.
 */
fun main() {
    val config = ConfigLoader.load()
    val engine = embeddedServer(
        factory = Netty,
        environment = applicationEnvironment {
            this.config = config
        },
        configure = {
            connector {
                port = config.property("deployment.port").getString().toInt()
            }
            connector {
                port = config.property("deployment.managementPort").getString().toInt()
            }
        },
    ) {
        tyzenhaus()
    }

    engine.addShutdownHook {
        @Suppress("MagicNumber")
        engine.stop(3, 5, TimeUnit.SECONDS)
    }

    engine.start(true)
}
