package dev.jeka.plugins.openapi;

import dev.jeka.core.api.depmanagement.JkDepSuggest;
import dev.jeka.core.api.depmanagement.JkRepoProperties;
import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.api.project.JkSourceGenerator;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.utils.JkUtilsString;
import dev.jeka.core.tool.JkBean;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.builtins.project.ProjectJkBean;

import java.nio.file.Path;

@JkDoc("Provides project configuration for generating code from openApi specifications.")
public class OpenApiJkBean extends JkBean {

    @JkDoc("The command line arguments in conjunction with 'exec' method.")
    public String cmdLine;

    @JkDoc("Version of openapi-generator-cli to use.")
    @JkDepSuggest(versionOnly = true, hint = "org.openapitools:openapi-generator:")
    public String cliVersion = JkOpenApiGeneratorCmd.DEFAULT_CLI_VERSION;

    @JkDoc("If true, the specified cmdLine will be run to generate sources at compile time")
    public boolean autoGenerate = true;

    @JkDoc("Execute openApi cli with argument specified in 'cmdArgs'.")
    public void exec() {
        exec(this.cmdLine);
    }

    @JkDoc("Display generic help about openApi cli options")
    public void cliHelp() {
        exec("help");
    }

    @JkDoc("Display help about available for 'generate' options")
    public void cliHelpGenerate() {
        exec("help generate");
    }

    @JkDoc("Display the available generators")
    public void cliList() {
        exec("list");
    }

    @JkDoc("Display config-help for spring server")
    public void cliConfigHelpSpring() {
        exec("config-help -g spring");
    }

    @JkDoc("Display config-help for java client")
    public void cliConfigHelpJavaClient() {
        exec("config-help -g java");
    }

    public OpenApiJkBean() {
        ProjectJkBean projectKBean = this.getRuntime().getBeanOptional(ProjectJkBean.class).orElse(null);
        if (projectKBean != null) {
            projectKBean.lately(project -> {
                if (autoGenerate && !JkUtilsString.isBlank(cmdLine)) {
                    project.compilation.addSourceGenerator(new CmdLineGenerator());
                }
            });
        }
    }

    public JkOpenApiSourceGenerator addSourceGenerator(JkProject project, String generatorName, String specLocation) {
        JkOpenApiSourceGenerator generator = JkOpenApiSourceGenerator.of(generatorName, specLocation)
                .setCliVersion(this.cliVersion);
        project.compilation.addSourceGenerator(generator);
        return generator;
    }


    private int exec(String cmdLine) {
        JkRepoProperties repoProperties = JkRepoProperties.of(this.getRuntime().getProperties());
        JkOpenApiGeneratorCmd cmd = JkOpenApiGeneratorCmd.of(repoProperties.getDownloadRepos(), cliVersion);
        return cmd.execCmdLine(cmdLine);
    }

    private class CmdLineGenerator extends JkSourceGenerator {

        @Override
        protected String getDirName() {
            return "openapi";
        }

        @Override
        protected void generate(JkProject project, Path generatedSourceDir) {
            JkOpenApiGeneratorCmd cmd = JkOpenApiGeneratorCmd.of(project.dependencyResolver.getRepos(), cliVersion);
            String effectiveCmdLine = cmdLine + " " + GenerateCmdBuilder.OUTPUT_PATH + " " + generatedSourceDir;
            if (JkLog.isVerbose()) {
                effectiveCmdLine = effectiveCmdLine + " --verbose";
            }
            cmd.execCmdLine(effectiveCmdLine);
        }

        @Override
        public String toString() {
            return "OpenapiKBeanGenerator";
        }
    }

}
