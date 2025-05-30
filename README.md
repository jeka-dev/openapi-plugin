![_dev.sample.Build Status](https://github.com/jeka-dev/openapi-plugin/actions/workflows/main.yml/badge.svg)
[![Maven Central](https://img.shields.io/maven-central/v/dev.jeka/openapi-plugin)](https://search.maven.org/search?q=g:%22dev.jeka%22%20AND%20a:%22openapi-plugin%22)

# Openapi generator for JeKa

A plugin to generate code from [OpenAPI](https://www.openapis.org/) definitions.

This plugin is a lightweight wrapper for [openapi-generator-cli](https://openapi-generator.tech/docs/usage) and provides:

- Automatic installation: Installs the OpenAPI generator for the specified version.
- Easy access to common commands.
- Automatic setup of OpenAPI generation as a project source generator.

Resources:
  - Command-line documentation: `jeka openapi: --doc` or `jeka -cp=dev.jeka:openapi-plugin:0.11.8-1 openapi: --doc`.
  - Source code: [Visit here](jeka-src/dev/jeka/plugins/openapi/OpenapiKBean.java).
  - OpenAPI: [Visit here](https://www.openapis.org/).
  - OpenAPI Generator: [Visit here](https://openapi-generator.tech/docs/usage).
  - OpenAPI Generator documentation: [Visit here](https://openapi-generator.tech/docs/usage/#generate).

## Configuration

You can append openapi source generator to your working project, just declaring properties in your *jeka.properties* file, 
following the below example:

```properties
# Import this plugin into JeKa classpath
jeka.classpath=dev.jeka:openapi-plugin:0.11.38-0

# By activating this plugin, project will automatically generate openapi code prior compilation.
@openapi=on

# Specifying the cli version is optional
@openapi.cliVersion=7.0.1

# Multiple generators can coexist in a single project by using different configuration keys.
# In this example, we use key="0".
@openapi.gen.config.0.inputSpec=https://petstore.swagger.io/v2/swagger.json
@openapi.gen.config.0.generatorName=spring
@openapi.gen.config.0.generationPackage=org.example.client
@openapi.gen.config.0.options.model-name-prefix=Rest
@openapi.gen.config.0.options.import-mappings=java.time.LocalDate=java.time.LocalDate
@openapi.gen.config.0.typeMappings.date=java.time.LocalDate
@openapi.gen.config.0.additionalProperties.useSpringBoot3=true

# It's also possible to specify the raw open-api command line.
@openapi.gen.cmdLine.my-client=generate -g java \
  --model-name-prefix Rest \
  --api-package org.example.client \
  --model-package org.example.client \
  -i https://petstore.swagger.io/v2/swagger.json \
  --library resttemplate \
  --additional-properties useJakartaEe=true
```

!!! Note:
    Intellij JeKa plugin provides auto-completion to suggest options and values.


See project example [here](sample-props/jeka.properties).

## Display openapi help

All available options can be listed using the provided methods:

Display openapi general help:
```shell
jeka openapi: helpCli
```

Display openapi 'generate' help:
```shell
jeka openapi: helpGenarate
```

Display list of available generators:
```shell
jeka openapi: helpListGenerators
```

Display options of a specific generator:
```shell
jeka openapi: helpGenerator helpGenerator=spring
```

## Programmatic Usage

This is also possible to configure open-api generation programmatically.

```java
String SPEC_URL = "https://petstore.swagger.io/v2/swagger.json";

// Create a source generator

JkOpenapiSourceGenerator openapiGen () {
    JkOpenapiSourceGenerator sourceGenerator = JkOpenapiSourceGenerator.of("spring", SPEC_URL);
    sourceGenerator.openapiCmd
            .addApiAndModelPackage("com.mycompany")
            .add(JkOpenapiCmdBuilder.MODEL_NAME_PREFIX, "Rest")
            .addAdditionalProperties("useSpringBoot3", "true")
            .add("--language-specific-primitives=Pet")
            .addImportMapping("Pet", "com.yourpackage.models.Pet")
            .addImportMapping("DateTime", "java.time.LocalDateTime")
            .addTypeMapping("DateTime", "java.time.LocalDateTime");
    return sourceGenerator;
}

@JkPostInit
private void postInit(ProjectKBean projectKBean) {
    JkProject project = projectKBean.project;
    project.compilation.addSourceGenerator(openapiGen());
}
```

See project example [here](sample-prog/jeka-src/SampleProgBuild.java).

_______________
## Contributors

The plugin code lies in *jeka-src* and so is built using the *base* KBean instead of *project*.

### How to release ?

Just use the [github release mechanism](https://github.com/jeka-dev/openapi-plugin/releases).
Creating a release consists in creating a tag, that will trigger a build and a publication on Maven Central.

