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

## Initialisation

The plugin scans properties named `openapi.gen.xxx`,
creates a source generator for each, and registers it to the `ProjectKBean`.

## Configuration

You can append openapi source generator to your working project, just declaring properties in your *jeka.properties* file, 
following the below example:

```properties
# Import this plugin into JeKa classpath
jeka.inject.classpath=dev.jeka:openapi-plugin:0.11.0-1

# Specify the version of openapi-generator-cli to set
@openapi=
@openapi.cliVersion=7.0.1

# Append a source generator, called 'myServer', to the project based on the following command line.
# The sources will be generated automatically prior compilation
# Any property formatted as openapi.gen.xxx will be taken in account
openapi.gen.myServer=generate -g spring \
  --model-name-prefix Rest \
  -i https://petstore.swagger.io/v2/swagger.json \
  --additional-properties=useBeanValidation=true,useSwaggerUI=false,interfaceOnly=true

# Append a second source generator
openapi.gen.myClient=generate -g client \
  --model-name-prefix Rest \
  -i https://my.spec.server/an-api.json
```

See project example [here](sample-props/jeka.properties).

## Programmatic Usage

Programmatic configuration may feel more natural to setup non-trivial structures. 

```java
String SPEC_URL = "https://petstore.swagger.io/v2/swagger.json";

// Create a source generator
JkOpenapiSourceGenerator sourceGenerator = JkOpenapiSourceGenerator.of("spring", SPEC_URL);
sourceGenerator.openapiCmd
        .addApiAndModelPackage("com.mycompany")
        .add(JkOpenapiCmdBuilder.MODEL_NAME_PREFIX, "Rest")
        .addAdditionalProperties("useSpringBoot3", "true")
        .add("--language-specific-primitives=Pet")
        .addImportMapping("Pet", "com.yourpackage.models.Pet")
        .addImportMapping("DateTime", "java.time.LocalDateTime")
        .addTypeMapping("DateTime", "java.time.LocalDateTime");

// Bind this generator to the project
JkProject project = myProject();
project.compilation.addSourceGenerator(project);
```

See project example [here](sample-prog/jeka-src/SampleProgBuild.java).

_______________
## Contributors

The plugin code lies in *jeka-src* and so is built using the *base* KBean instead of *project*.

### How to release ?

Just use the [github release mechanism](https://github.com/jeka-dev/openapi-plugin/releases).
Creating a release consists in creating a tag, that will trigger a build and a publication on Maven Central.

