package sample;

import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.tool.JkBean;
import dev.jeka.core.tool.builtins.project.ProjectJkBean;
import dev.jeka.plugins.openapi.OpenApiJkBean;

/**
 * Sample using only properties of OpenApiJkBean
 */
public class SampleBuild_PropertiesOnly extends JkBean {

    ProjectJkBean projectBean = getBean(ProjectJkBean.class);

    OpenApiJkBean openApi = getBean(OpenApiJkBean.class);

    SampleBuild_PropertiesOnly() {

        // we cannot inject properties from code, so we need to test using field value only.
        openApi.cmdLine =
                "generate -g spring " +
                "--model-name-prefix Rest " +
                "-i https://petstore.swagger.io/v2/swagger.json " +
                "--additional-properties=base.package=org.myapp,useBeanValidation=true,sourceFolder=/," +
                "useSwaggerUI=false,interfaceOnly=true";
    }

    // For testing purpose only
    public void gen() {
        JkPathTree.of(projectBean.getProject().getOutputDir()).deleteContent();
        projectBean.getProject().compilation.skipJavaCompilation();
        projectBean.getProject().compilation.run();
    }

}
