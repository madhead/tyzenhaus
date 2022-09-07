package me.madhead.tyzenhaus.launcher.fly.stats.koin

import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import org.koin.dsl.module

val influxModule = module {
    single {
        InfluxDBClientKotlinFactory.create(
            url = System.getenv("INFLUX_URL")!!,
            token = System.getenv("INFLUX_TOKEN")!!.toCharArray(),
            org = System.getenv("INFLUX_ORG")!!,
            bucket = System.getenv("INFLUX_BUCKET")!!,
        )
    }
}
