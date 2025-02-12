plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    alias(libs.plugins.pluginPublish)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    implementation(libs.flatgraphDomainClassesGenerator)

    testImplementation(platform(libs.junitPlatform))
    testImplementation(libs.junitEngine)
    testRuntimeOnly(libs.junitPlatformLauncher)
}

kotlin {
    jvmToolchain(17)
}

gradlePlugin {
    plugins {
        create(property("ID").toString()) {
            id = property("ID").toString()
            implementationClass = property("IMPLEMENTATION_CLASS").toString()
            version = scmVersion.version
            description = property("DESCRIPTION").toString()
            displayName = property("DISPLAY_NAME").toString()
            tags.set(listOf("codegen", "flatgraph", "scala", "joern", "schema"))
        }
    }
}

gradlePlugin {
    website.set(property("WEBSITE").toString())
    vcsUrl.set(property("VCS_URL").toString())
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/stackgenhq/flatgraph-codegen-gradle")
            credentials {
                username = project.findProperty("gradle.publish.key") as String? ?: System.getenv("GRADLE_PUBLISH_KEY")
                password = project.findProperty("gradle.publish.secret") as String? ?: System.getenv("GRADLE_PUBLISH_SECRET")
            }
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }

    register("setupPluginUploadFromEnvironment") {
        doLast {
            val key = System.getenv("GRADLE_PUBLISH_KEY")
            val secret = System.getenv("GRADLE_PUBLISH_SECRET")

            if (key == null || secret == null) {
                throw GradleException("gradlePublishKey and/or gradlePublishSecret are not defined environment variables")
            }

            System.setProperty("gradle.publish.key", key)
            System.setProperty("gradle.publish.secret", secret)
        }
    }
}
