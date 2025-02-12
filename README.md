# Flatgraph Codegen Gradle

A Gradle version of the Flatgraph codegen sbt plugin

## Usage

Configure the task in your `build.gradle.kts`, eg:
```kotlin
tasks.named<GenerateDomainClassesTask>("generateDomainClasses") {
    classWithSchema = "CpgExtSchema$"
    fieldName = "instance"
    outputDirectory = file("build/generated/flatgraph/main/scala")
}
```

...where your `src/main/scala` directory contains the `CpgExtSchema` object with the `instance` field containing any
desired schema extensions.
```scala
import flatgraph.schema.SchemaBuilder
import flatgraph.schema.Property.ValueType
import io.shiftleft.codepropertygraph.schema.CpgSchema

class CpgExtSchema(builder: SchemaBuilder, cpgSchema: CpgSchema) {

  // Add node types, edge types, and properties here

  val myProperty = builder
    .addProperty(name = "MYPROPERTY", valueType = ValueType.String)
    .mandatory("")

  val myNodeType = builder
    .addNodeType("MYNODETYPE")
    .addProperty(myProperty)

}

object CpgExtSchema {
  val builder   = new SchemaBuilder(domainShortName = "Cpg", basePackage = "flatgraph.generated")
  val cpgSchema = new CpgSchema(builder)
  val cpgExtSchema = new CpgExtSchema(builder, cpgSchema)
  val instance     = builder.build
}
```

See also [example](example/build.gradle.kts)
