plugins {
    scala
    id("com.stackgen.flatgraph.plugin")
}

dependencies {
    implementation("org.scala-lang:scala3-library_3:3.4.2")
    implementation("io.shiftleft:codepropertygraph-schema_3:1.7.1")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

val scalaDomainClassesGeneratedOutputDir by lazy { file("build/generated/flatgraph/main/scala") }

tasks {
    generateDomainClasses {
        dependsOn(compileScala) // make sure we've compiled our schema class before attempting to codegen

        classWithSchema = "CpgExtSchema$"
        fieldName = "instance"
        outputDirectory = scalaDomainClassesGeneratedOutputDir
    }
}
