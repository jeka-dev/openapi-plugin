import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.tool.*;
import dev.jeka.core.tool.builtins.project.ProjectKBean;
import dev.jeka.plugins.openapi.JkOpenApiSourceGenerator;
import dev.jeka.plugins.openapi.OpenapiKBean;
import dev.jeka.plugins.springboot.SpringbootKBean;

@JkDep("../.jeka-work/jeka-src-classes")
@JkDep("dev.jeka:springboot-plugin")
class SampleProgBuild extends KBean {

    @JkPostInit(required = true)
    private void postInit(SpringbootKBean springbootKBean) {
    }

    @JkPostInit(required = true)
    private void postInit(OpenapiKBean openapiKBean) {
        openapiKBean.cliVersion = "7.0.1";
    }

    @JkPostInit
    private void postInit(ProjectKBean projectKBean) {
        JkProject project = projectKBean.project;

        project.flatFacade.dependencies.compile
                .add("org.springframework.boot:spring-boot-dependencies::pom:3.4.0")
                .add("org.springframework.boot:spring-boot-starter-web")
                .add("org.springdoc:springdoc-openapi-common:1.7.0")
                .add("org.openapitools:jackson-databind-nullable:0.2.6");
        project.flatFacade.dependencies.runtime
                .add("org.springframework.boot:spring-boot-starter-test");

        JkOpenApiSourceGenerator sourceGenerator = JkOpenApiSourceGenerator.of("spring",
                "https://petstore.swagger.io/v2/swagger.json");
        sourceGenerator.openapiCmd
                .add("--model-name-prefix", "Rest")
                .addApiAndModelPackage("org.example.server")
                .addAdditionalProperties("useSpringBoot3", "true")
                .addAdditionalProperties("interfaceOnly", "true")
                .addAdditionalProperties("useBeanValidation", "true")
                .addAdditionalProperties("singleContentTypes", "true");
        project.compilation.addSourceGenerator(sourceGenerator);
    }

}