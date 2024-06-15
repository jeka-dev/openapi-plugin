package _dev.sample;

import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.plugins.openapi.JkOpenApiSourceGenerator;
import dev.jeka.plugins.openapi.JkOpenapiCmdBuilder;

/**
 * Sample using only properties of OpenApiJkBean
 */
public class SampleBuild_PureProgrammatic {

    private static final String SPEC_URL = "https://petstore.swagger.io/v2/swagger.json";

    public static void main(String[] args) {
        JkLog.setDecorator(JkLog.Style.DEBUG);
        JkProject project = JkProject.of();
        JkOpenApiSourceGenerator generator = JkOpenApiSourceGenerator.of("spring", SPEC_URL)
                .setCliVersion("7.0.1")
                .customize(cmdBuilder -> cmdBuilder
                    .addApiAndModelPackage("com.mycompany")
                    .add(JkOpenapiCmdBuilder.MODEL_NAME_PREFIX, "Rest")
                    .add("--language-specific-primitives=Pet")
                    .addImportMapping("Pet", "com.yourpackage.models.Pet")
                    .addImportMapping("DateTime", "java.time.LocalDateTime")
                    .addTypeMapping("DateTime", "java.time.LocalDateTime")
                );
        project.compilation.addSourceGenerator(generator);
        project.compilation.generateSources();
    }


}
