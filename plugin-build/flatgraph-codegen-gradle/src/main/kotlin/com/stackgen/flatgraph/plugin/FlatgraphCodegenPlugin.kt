package com.stackgen.flatgraph.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class FlatgraphCodegenPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("generateDomainClasses", GenerateDomainClassesTask::class.java) {
            it.group = "other"
            it.description = "Generates flatgraph domain classes"
        }
    }
}
