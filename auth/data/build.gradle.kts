plugins {
    alias(libs.plugins.runique.android.library)
}

android {
    namespace = "com.juandgaines.auth.data"
}

dependencies {

    implementation(projects.auth.domain)
    implementation(projects.core.domain)
    implementation(projects.core.data)

}