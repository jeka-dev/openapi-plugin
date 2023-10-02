package sample;

import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.tool.JkBean;
import dev.jeka.core.tool.builtins.project.ProjectJkBean;
import dev.jeka.plugins.openapi.GenerateCmdBuilder;
import dev.jeka.plugins.openapi.OpenApiJkBean;

/**
 * Sample using only properties of OpenApiJkBean
 */
public class SampleBuild_Programmatic extends JkBean {

    private static final String SPEC_URL = "https://petstore.swagger.io/v2/swagger.json";

    ProjectJkBean projectBean = getBean(ProjectJkBean.class).lately(this::configure);

    OpenApiJkBean openApi = getBean(OpenApiJkBean.class);

    private void configure(JkProject project) {
        openApi.addSourceGenerator(project, "spring", SPEC_URL).customize(cmdBuilder -> cmdBuilder
                .addApiAndModelPackage("com.mycompany")
                .add(GenerateCmdBuilder.MODEL_NAME_PREFIX, "Rest")
        );
    }

    public void gen() {
        cleanOutput();
        projectBean.getProject().compilation.generateSources();
    }

}
