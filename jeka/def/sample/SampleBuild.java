package sample;

import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.tool.JkBean;
import dev.jeka.core.tool.builtins.project.ProjectJkBean;
import dev.jeka.plugins.openapi.OpenApiJkBean;

public class SampleBuild extends JkBean {

    OpenApiJkBean openApi = getBean(OpenApiJkBean.class);

    JkProject project;

    SampleBuild() {
        openApi.definitionFile = "https://petstore.swagger.io/v2/swagger.json";
        //openApi.packageName = "org.example";
        openApi.cmdArgs= "-g spring --model-name-prefix Rest " +
                "--additional-properties=base.package=org.myapp,useBeanValidation=true,sourceFolder=/," +
                "useSwaggerUI=false,interfaceOnly=true";
        //openApi.cmdArgs = "-g java";

        project = JkProject.of();
        project.flatFacade()
                .setBaseDir(getBaseDir().resolve("sample"));
        project.compilation.addSourceGenerator(openApi.getSourceGenerator());
        project.compilation.skipJavaCompiilation();
    }

    public void gen() {
        JkPathTree.of(project.getOutputDir()).deleteContent();
        project.compilation.run();
    }

}
