import com.android.build.api.dsl.DynamicFeatureExtension
import com.juandgaines.convention.ExtensionType
import com.juandgaines.convention.addUiLayerDependencies
import com.juandgaines.convention.configureAndroidCompose
import com.juandgaines.convention.configureBuildTypes
import com.juandgaines.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidDynamicFeatureConventionPlugin:Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.dynamic-feature")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.compose")
            }
            extensions.configure<DynamicFeatureExtension>{
                configureKotlinAndroid(this)
                configureAndroidCompose(this)

                configureBuildTypes(this, ExtensionType.DYNAMIC_FEATURE)
            }

            dependencies{
                addUiLayerDependencies(target)
                "testImplementation"(kotlin("test"))
            }
        }
    }
}