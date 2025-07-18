plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.kotlinMultiplatform.library) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt)
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "com.diffplug.spotless")

    detekt {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom(files("$rootDir/config/detekt.yml"))
        baseline = file("$rootDir/config/baseline.xml")

        tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
            reports {
                html.required.set(true)
                xml.required.set(true)
                txt.required.set(true)
                md.required.set(true)
            }
        }
    }

    spotless {
        kotlin {
            target("**/*.kt")
            targetExclude("!**/build/**/*.*", "**/generated/**/*.kt", "**/MainViewController.kt")
            ktlint(libs.versions.ktlint.get())
                .editorConfigOverride(
                    mapOf(
                        "indent_size" to "4",
                        "max_line_length" to "120",
                        "ktlint_standard_function-expression-body" to "disabled",
                        "ktlint_standard_class-signature" to "disabled",
                        "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                        "ktlint_compose_compositionlocal-allowlist" to "disabled"
                    ),
                )
                .customRuleSets(listOf("io.nlopez.compose.rules:ktlint:0.4.22"))
            trimTrailingWhitespace()
            endWithNewline()
        }

        kotlinGradle {
            target("**/*.gradle.kts")
            ktlint(libs.versions.ktlint.get())
                .editorConfigOverride(
                    mapOf("indent_size" to "4", "max_line_length" to "120"),
                )
            trimTrailingWhitespace()
            endWithNewline()
        }
    }

    tasks.register("analyzeCode") {
        group = "verification"
        description = "Runs static code analysis (detekt)"
        dependsOn(tasks.named("detekt"))
    }

    tasks.register("checkFormatting") {
        group = "verification"
        description = "Checks code formatting (spotless)"
        dependsOn(tasks.named("spotlessCheck"))
    }

    tasks.register("formatCode") {
        group = "formatting"
        description = "Formats code using spotless"
        dependsOn(tasks.named("spotlessApply"))
    }

    tasks.register("validateCode") {
        group = "verification"
        description = "Runs all code quality checks and formatting verification"
        dependsOn(tasks.named("analyzeCode"), tasks.named("checkFormatting"))
    }
}

abstract class InstallGitHooksTask : DefaultTask() {
    @InputDirectory
    val hooksDir: DirectoryProperty = project.objects.directoryProperty()

    @OutputDirectory
    val gitHooksDir: DirectoryProperty = project.objects.directoryProperty()

    init {
        hooksDir.set(project.rootProject.layout.projectDirectory.dir("config/hooks"))
        gitHooksDir.set(project.rootProject.layout.projectDirectory.dir(".git/hooks"))
    }

    @TaskAction
    fun install() {
        val hooks = hooksDir.get().asFile
        val targetDir = gitHooksDir.get().asFile

        if (hooks.exists()) {
            hooks.listFiles()?.forEach { hookFile ->
                if (hookFile.isFile) {
                    val targetFile = File(targetDir, hookFile.name)
                    hookFile.copyTo(targetFile, overwrite = true)
                    targetFile.setExecutable(true)
                    println("Installed git hook: ${hookFile.name}")
                }
            }
        } else {
            throw GradleException("Hooks directory not found: ${hooks.absolutePath}")
        }
    }
}

tasks.register<InstallGitHooksTask>("installGitHooks") {
    group = "git hooks"
    description = "Installs git hooks from the config/hooks directory"
}
