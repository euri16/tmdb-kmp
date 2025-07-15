plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow.gradle)
    application
}

application {
    mainClass.set("dev.euryperez.tmdb.mcpserver.MainKt")
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "dev.euryperez.tmdb.mcpserver.MainKt"
    }
    archiveClassifier.set("") // This removes the "-all" suffix
    mergeServiceFiles() // Important for some libraries
}

// Make build depend on shadowJar instead of jar
tasks.build {
    dependsOn(tasks.shadowJar)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(projects.core.models)
    implementation(projects.data.common)
    implementation(projects.data.movies)

    implementation(libs.mcp.kotlin)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization)
}