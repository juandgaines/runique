package com.juandgaines.convention

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import com.android.build.api.dsl.BuildType

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*, *, *, *, *>,
    extensionType: ExtensionType
){
    commonExtension.run {
        buildFeatures {
            buildConfig = true
        }
        val apiKey = gradleLocalProperties(rootDir).getProperty("API_KEY")
        val mapApiKeyDebug = gradleLocalProperties(rootDir).getProperty("MAPS_KEY_DEBUG")
        val mapApiKeyRelease = gradleLocalProperties(rootDir).getProperty("MAPS_KEY_RELEASE")

        when (extensionType) {
            ExtensionType.APPLICATION -> {
                extensions.configure<com.android.build.api.dsl.ApplicationExtension> {

                    buildTypes {
                        debug {
                            configureDebugBuildType(apiKey, mapApiKeyDebug)
                        }
                        release {
                            configureReleaseBuildType(commonExtension, apiKey, mapApiKeyRelease)
                        }
                    }
                }
            }
            ExtensionType.LIBRARY -> {
                extensions.configure<com.android.build.api.dsl.LibraryExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(apiKey, mapApiKeyDebug)
                        }
                        release {
                            configureReleaseBuildType(commonExtension, apiKey, mapApiKeyRelease)
                        }
                    }
                }
            }
        }
    }
}

private fun BuildType.configureDebugBuildType(
    apiKey: String,
    mapApiKeyDebug: String
) {
    buildConfigField("String", "API_KEY", "\"$apiKey\"")
    buildConfigField("String", "BASE_URL", "\"https://runique.pl-coding.com:8080\"")
    manifestPlaceholders["MAPS_API_KEY"] = mapApiKeyDebug
}

private fun BuildType.configureReleaseBuildType(
    commonExtension: CommonExtension<*, *, *, *, *>,
    apiKey: String,
    mapApiKeyRelease: String
) {
    buildConfigField("String", "API_KEY", "\"$apiKey\"")
    buildConfigField("String", "BASE_URL", "\"https://runique.pl-coding.com:8080\"")
    manifestPlaceholders["MAPS_API_KEY"] = mapApiKeyRelease

    isMinifyEnabled = true
    proguardFiles(
        commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}