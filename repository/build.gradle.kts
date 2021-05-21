plugins {
    kotlin("jvm")
}

dependencies {
    libs.bundles.boms.orNull?.forEach {
        api(platform(it))
    }

    api(projects.entity)
}
