plugins {
    alias(libs.plugins.runique.jvm.library)
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.kotlinx.coroutines.core)
}