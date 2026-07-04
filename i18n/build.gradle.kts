plugins {
    id("kotlin-convention")
}

dependencies {
    libs.bundles.boms.orNull?.forEach {
        api(platform(it))
    }
}
