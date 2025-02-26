import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.axionRelease)
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.pluginPublish) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.versionCheck)
}

allprojects {
    apply {
        plugin(rootProject.libs.plugins.detekt.get().pluginId)
        plugin(rootProject.libs.plugins.ktlint.get().pluginId)
        plugin(rootProject.libs.plugins.axionRelease.get().pluginId)
    }

    scmVersion {
        versionIncrementer("incrementMinorIfNotOnRelease", mapOf("releaseBranchPattern" to "release/.+"))
        repository {
            directory.set(project.rootProject.projectDir.resolve("../").path)
        }
    }

    group = property("GROUP").toString()
    version = scmVersion.version

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
        config.setFrom(rootProject.files("../config/detekt/detekt.yml"))
    }
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(file("build/reports/detekt.html"))
    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.layout.buildDirectory)
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}
