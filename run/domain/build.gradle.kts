plugins {
    alias(libs.plugins.runique.jvm.library)
    alias(libs.plugins.runique.jvm.junit5)
}
dependencies {
    implementation(projects.core.domain)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(projects.core.test)
    implementation(projects.core.connectivity.domain)
}