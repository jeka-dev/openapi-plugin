![Build Status](https://github.com/jeka-dev/openapi-plugin/actions/workflows/main.yml/badge.svg)
[![Maven Central](https://img.shields.io/maven-central/v/dev.jeka/openapi-plugin)](https://search.maven.org/search?q=g:%22dev.jeka%22%20AND%20a:%22openapi-plugin%22)

# Openapi generator for JeKa

Openapi plugin for [JeKa](https://jeka.dev) acts as a thin wrapper around [openapi-generator-cli](https://openapi-generator.tech/docs/usage).

It handles on-the-fly installation for you, so you only have to specify which version you want to use.

This plugin offers several out-of-the-box commands to invoke *openapi-generator-cli* conveniently. 
To see list of provided commands and options, execute :

```bash
jeka @dev.jeka:openapi-plugin:0.10.26.1 openapi#help
````
Furthermore, this plugin offers convenient methods to link source code generation with any project KBean.

The generation match strictly the openapi-generator-cli syntax, so you can refer to [the official documentation](https://openapi-generator.tech/docs/usage/#generate) 
to generate sources for any target technology without limitations.

## Using local.properties

You can append openapi source generator to your working project, just declaring properties in your *local.properties* file, following the below example.

```properties
# Import this plugin into JeKa classpath
jeka.cmd._append=@dev.jeka:openapi-plugin:0.10.28-0 project#

# Specify the version of openapi-generator-cli to uset
openapi#cliVersion=7.0.1

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

See project example [here](./sample-props).

## Using programmatic method

Alternativelly, you can leverage of the fluent api to link your built project with an openapi 
source code generator, as demonstrated below.

```java
import dev.jeka.core.tool.JkInjectClasspath;

@JkInjectClasspath("dev.jeka:openapi-plugin:0.10.28-0")
public class SampleBuild_Programmatic extends JkBean {

    private static final String SPEC_URL = "https://petstore.swagger.io/v2/swagger.json";

    ProjectJkBean projectBean = getBean(ProjectJkBean.class).lately(this::configure);

    OpenapiJkBean openApi = getBean(OpenapiJkBean.class).setCliVersion("7.0.1");

    private void configure(JkProject project) {
        openApi.addSourceGenerator(project, "spring", SPEC_URL).customize(cmdBuilder -> cmdBuilder
                .addApiAndModelPackage("com.mycompany")
                .add(GenerateCmdBuilder.MODEL_NAME_PREFIX, "Rest")
                .add("--language-specific-primitives=Pet")
                .addImportMapping("Pet", "com.yourpackage.models.Pet")
                .addImportMapping("DateTime", "java.time.LocalDateTime")
                .addTypeMapping("DateTime", "java.time.LocalDateTime")
        );
    }

}
```

See project example [here](./sample-props).

## Developers

### How to release ?

Just use the [github release mechanism](https://github.com/jeka-dev/openapi-plugin/releases).
Creating a release implies creating a tag, that will trigger a build and a publication on Maven Central.





