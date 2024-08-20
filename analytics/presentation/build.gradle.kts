plugins {
    alias(libs.plugins.runique.android.feature.ui)
}

android {
    namespace = "com.juandgaines.analytics.presentation"
}

dependencies {
    implementation(projects.analytics.domain)
}