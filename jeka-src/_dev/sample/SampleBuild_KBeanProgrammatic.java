package _dev.sample;

import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.tool.KBean;
import dev.jeka.plugins.openapi.JkOpenApi;
import dev.jeka.plugins.openapi.JkOpenapiGenerateBuilder;

/**
 * Sample using only properties of OpenApiJkBean
 */
public class SampleBuild_KBeanProgrammatic extends KBean {

    private static final String SPEC_URL = "https://petstore.swagger.io/v2/swagger.json";

    private static final String OPENAPI_CLI_VERSION = "7.0.1";


    private JkProject project() {
        JkProject project = JkProject.of();
        //...
        JkOpenApi.ofVersion(OPENAPI_CLI_VERSION).addSourceGenerator(project, "spring", SPEC_URL)
            .openapiCmd
                .addApiAndModelPackage("com.mycompany")
                .add(JkOpenapiGenerateBuilder.MODEL_NAME_PREFIX, "Rest")
                .addAdditionalProperties("useSpringBoot3", "true")
                .add("--language-specific-primitives=Pet")
                .addImportMapping("Pet", "com.yourpackage.models.Pet")
                .addImportMapping("DateTime", "java.time.LocalDateTime")
                .addTypeMapping("DateTime", "java.time.LocalDateTime");
        return project;
    }

    public void genCode() {
        project().compilation.generateSources();
    }

}
