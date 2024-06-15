import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.JkInjectClasspath;
import dev.jeka.core.tool.KBean;
import dev.jeka.core.tool.builtins.project.ProjectKBean;
import dev.jeka.plugins.openapi.JkOpenApiSourceGenerator;
import dev.jeka.plugins.openapi.OpenapiKBean;
import dev.jeka.plugins.springboot.SpringbootKBean;

@JkInjectClasspath("../.jeka-work/jeka-src-classes")
@JkInjectClasspath("dev.jeka:springboot-plugin")
class SampleProgBuild extends KBean {

    final SpringbootKBean springbootBean = load(SpringbootKBean.class);

    final OpenapiKBean openapiKBean = load(OpenapiKBean.class);

    protected void init() {
        openapiKBean.cliVersion = "7.0.1";
        JkProject project = load(ProjectKBean.class).project;
        project.flatFacade()
            .customizeCompileDeps(deps -> deps
                    .and("org.springframework.boot:spring-boot-dependencies::pom:3.3.0")
                    .and("org.springframework.boot:spring-boot-starter-web")
                    .and("org.springdoc:springdoc-openapi-common:1.7.0")
                    .and("org.openapitools:jackson-databind-nullable:0.2.6")
            )
            .customizeTestDeps(deps -> deps
                    .and("org.springframework.boot:spring-boot-starter-test")
            );
        JkOpenApiSourceGenerator sourceGenerator = JkOpenApiSourceGenerator.of("spring",
                        "https://petstore.swagger.io/v2/swagger.json");
        sourceGenerator.customize(cmdBuilder -> cmdBuilder
                        .add("--model-name-prefix", "Rest")
                        .addApiAndModelPackage("org.example.server")
                        .addAdditionalProperties("useSpringBoot3", "true")
                        .addAdditionalProperties("interfaceOnly", "true")
                        .addAdditionalProperties("useBeanValidation", "true")
                        .addAdditionalProperties("singleContentTypes", "true")
                );
        project.compilation.addSourceGenerator(sourceGenerator);
    }

    @JkDoc("Cleans, tests and creates bootable jar.")
    public void cleanPack() {
        super.cleanOutput();
        load(ProjectKBean.class).project.pack();
    }

}