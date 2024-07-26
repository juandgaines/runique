plugins {
    alias(libs.plugins.runique.android.library.compose)
}

android {
    namespace = "com.juandgaines.auth.presentation"
}

dependencies {

    implementation(projects.core.domain)
    implementation(projects.auth.domain)

}