package _dev.sample;

import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.tool.KBean;
import dev.jeka.core.tool.builtins.project.ProjectKBean;
import dev.jeka.plugins.openapi.OpenapiKBean;

/**
 * Sample using only properties of OpenApiJkBean
 */
public class SampleBuild_PropertiesOnly extends KBean {

    JkProject project = load(ProjectKBean.class).project;

    OpenapiKBean openApi = load(OpenapiKBean.class);

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
        JkPathTree.of(project.getOutputDir()).deleteContent();
        project.compilation.skipJavaCompilation();
        project.compilation.run();
    }

}
