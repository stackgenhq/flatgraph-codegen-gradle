import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.gitlab.arturbosch.detekt.Detekt
import pl.allegro.tech.build.axion.release.domain.hooks.HookContext
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.axionRelease)
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.versionCheck)
}

scmVersion {
    versionIncrementer("incrementMinorIfNotOnRelease", mapOf("releaseBranchPattern" to "release/.+"))

    hooks {
        // Automate moving `[Unreleased]` changelog entries into `[<version>]` on release
        // NOTE: Assumes rootProject.name == Github repo name
        // FIXME - workaround for Kotlin DSL issue https://github.com/allegro/axion-release-plugin/issues/500
        val changelogPattern =
            "\\[Unreleased\\]([\\s\\S]+?)\\n" +
                "(?:^\\[Unreleased\\]: https:\\/\\/github\\.com\\/(\\S+\\/\\S+)\\/compare\\/[^\\n]*\$([\\s\\S]*))?\\z"
        pre(
            "fileUpdate",
            mapOf(
                "file" to "CHANGELOG.md",
                "pattern" to KotlinClosure2<String, HookContext, String>({ _, _ -> changelogPattern }),
                "replacement" to KotlinClosure2<String, HookContext, String>({ version, context ->
                    // github "diff" for previous version
                    val previousVersionDiffLink =
                        when (context.previousVersion == version) {
                            true -> "releases/tag/v$version" // no previous, just link to the version
                            false -> "compare/v${context.previousVersion}...v$version"
                        }
                    """
                        \[Unreleased\]

                        ## \[$version\] - $currentDateString$1
                        \[Unreleased\]: https:\/\/github\.com\/$2\/compare\/v$version...HEAD
                        \[$version\]: https:\/\/github\.com\/$2\/$previousVersionDiffLink$3
                    """.trimIndent()
                }),
            ),
        )

        pre("commit")
    }
}

subprojects {
    apply {
        plugin(rootProject.libs.plugins.detekt.get().pluginId)
        plugin(rootProject.libs.plugins.ktlint.get().pluginId)
    }

    ktlint {
        debug.set(false)
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        ignoreFailures.set(false)
        enableExperimentalRules.set(true)
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }

    detekt {
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    }
}

val currentDateString: String
    get() = OffsetDateTime.now(ZoneOffset.UTC).toLocalDate().format(DateTimeFormatter.ISO_DATE)

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(file("build/reports/detekt.html"))
    }
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        candidate.version.isNonStable()
    }
}

fun String.isNonStable() = "^[0-9,.v-]+(-r)?$".toRegex().matches(this).not()

tasks.register("clean", Delete::class.java) {
    delete(rootProject.layout.buildDirectory)
}

tasks.register("reformatAll") {
    description = "Reformat all the Kotlin Code"

    dependsOn("ktlintFormat")
    dependsOn(gradle.includedBuild("plugin-build").task(":flatgraph-codegen-gradle:ktlintFormat"))
}

tasks.register("preMerge") {
    description = "Runs all the tests/verification tasks on both top level and included build."

    dependsOn(":example:check")
    dependsOn(gradle.includedBuild("plugin-build").task(":flatgraph-codegen-gradle:check"))
    dependsOn(gradle.includedBuild("plugin-build").task(":flatgraph-codegen-gradle:validatePlugins"))
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}
