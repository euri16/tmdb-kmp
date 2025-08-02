plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonTest {
            dependencies {
                // Core modules
                implementation(projects.core.models)
                implementation(projects.core.network)
                implementation(projects.core.test.testCommon)
                implementation(projects.core.utils)

                // Data modules
                implementation(projects.data.common)
                implementation(projects.data.movies)

                // Testing dependencies
                implementation(libs.test.kotlin)
                implementation(libs.test.kotlin.annotations.common)
                implementation(libs.test.coroutines)
                implementation(libs.test.turbine)

                // Networking for integration tests
                implementation(libs.ktor.client.core)
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.datetime)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.test.kotlin)
                implementation(libs.ktor.client.okhttp)
            }
        }

        iosTest {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}
