plugins {
    alias(libs.plugins.runique.android.library)
    alias(libs.plugins.runique.android.room)
}

android {
    namespace = "com.juandgaines.analytics.data"
}

dependencies {

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.bundles.koin)
    implementation(projects.core.data)
    implementation(projects.core.database)
    implementation(projects.analytics.domain)
}