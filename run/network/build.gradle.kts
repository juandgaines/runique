plugins {
    alias(libs.plugins.runique.android.library)
}

android {
    namespace = "com.juandgaines.run.network"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)

}