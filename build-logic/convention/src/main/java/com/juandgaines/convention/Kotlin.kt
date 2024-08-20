package com.juandgaines.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion.VERSION_17
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.internal.impldep.com.jcraft.jsch.ConfigRepository.defaultConfig
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *,*,*>
){
    commonExtension.apply {
        compileSdk = libs.findVersion("projectCompileSdkVersion").get().toString().toInt()
        defaultConfig.minSdk = libs.findVersion("projectMinSdkVersion").get().toString().toInt()

        compileOptions {
            isCoreLibraryDesugaringEnabled = true
            sourceCompatibility = VERSION_17
            targetCompatibility = VERSION_17
        }

    }

    dependencies {
        "coreLibraryDesugaring"(libs.findLibrary("desugar.jdk.libs").get())
    }

}

internal fun Project.configureKotlinJvm(){
    extensions.configure<JavaPluginExtension>{
        sourceCompatibility = VERSION_17
        targetCompatibility = VERSION_17
    }
}

private fun Project.configureKotlin(){
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = VERSION_17.toString()
        }
    }
}