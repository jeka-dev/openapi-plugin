import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.tool.JkBean;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.JkInjectClasspath;
import dev.jeka.plugins.openapi.OpenapiJkBean;
import dev.jeka.plugins.springboot.SpringbootJkBean;

@JkInjectClasspath("../jeka/.work/def-classes")
@JkInjectClasspath("dev.jeka:springboot-plugin")
class SampleProgBuild extends JkBean {

    final SpringbootJkBean springbootBean = getBean(SpringbootJkBean.class);

    final OpenapiJkBean openapiKBean = getBean(OpenapiJkBean.class);

    SampleProgBuild() {
        springbootBean.setSpringbootVersion("3.1.4");
        springbootBean.projectBean.lately(this::configure);
        openapiKBean.cliVersion = "7.0.1";
    }

    private void configure(JkProject project) {
        project.flatFacade()
            .configureCompileDependencies(deps -> deps
                    .and("org.springframework.boot:spring-boot-starter-web")
                    .and("org.springdoc:springdoc-openapi-common:1.7.0")
                    .and("org.openapitools:jackson-databind-nullable:0.2.6")
            )
            .configureTestDependencies(deps -> deps
                    .and("org.springframework.boot:spring-boot-starter-test")
            );
        openapiKBean.addSourceGenerator(project, "spring", "https://petstore.swagger.io/v2/swagger.json")
                .customize(cmdBuilder -> cmdBuilder
                        .add("--model-name-prefix", "Rest")
                        .addApiAndModelPackage("org.example.server")
                        .addAdditionalProperties("useSpringBoot3", "true")
                        .addAdditionalProperties("interfaceOnly", "true")
                        .addAdditionalProperties("useBeanValidation", "true")
                        .addAdditionalProperties("singleContentTypes", "true")
                );
    }

    @JkDoc("Cleans, tests and creates bootable jar.")
    public void cleanPack() {
        springbootBean.projectBean.cleanPack();
    }

}