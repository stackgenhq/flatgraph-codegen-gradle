package com.stackgen.flatgraph.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

@CacheableTask
abstract class GenerateDomainClassesTask
    @Inject
    constructor(
        @Internal val execOperations: ExecOperations,
    ) : DefaultTask() {
        @get:Input
        abstract val classWithSchema: Property<String>

        @get:Input
        abstract val fieldName: Property<String>

        @get:Input
        abstract val outputDirectory: Property<File>

        @get:InputFiles
        @PathSensitive(PathSensitivity.RELATIVE)
        val projectSourceFiles: FileCollection = project.fileTree("src")

        @get:InputFile
        @PathSensitive(PathSensitivity.NONE)
        val buildScript: File = project.buildFile

        @get:OutputDirectory
        val output: Provider<File> by lazy { outputDirectory }

        @TaskAction
        fun action() {
            val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)

            // exec as a subprocess rather than directly invoking `flatgraph.codegen.Main.main(...)` as we won't have
            // the necessary classes on the classpath in this process
            execOperations.javaexec {
                it.mainClass.set("flatgraph.codegen.Main")
                it.classpath = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).runtimeClasspath
                it.args =
                    listOf(
                        "--classWithSchema",
                        classWithSchema.get(),
                        "--field",
                        fieldName.get(),
                        "--out",
                        outputDirectory.get().path,
                        "--noformat",
                    )
            }
        }
    }
