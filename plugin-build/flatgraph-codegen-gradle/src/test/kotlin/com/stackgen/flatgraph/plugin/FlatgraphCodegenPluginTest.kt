package com.stackgen.flatgraph.plugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class FlatgraphCodegenPluginTest {
    @Test
    fun `plugin is applied correctly to the project`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.stackgen.flatgraph.plugin")

        assert(project.tasks.getByName("generateDomainClasses") is GenerateDomainClassesTask)
    }
}
