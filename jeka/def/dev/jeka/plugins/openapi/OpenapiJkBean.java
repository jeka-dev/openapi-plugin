package dev.jeka.plugins.openapi;

import dev.jeka.core.api.depmanagement.JkDepSuggest;
import dev.jeka.core.api.depmanagement.JkRepoProperties;
import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.api.project.JkSourceGenerator;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.system.JkProperties;
import dev.jeka.core.tool.JkBean;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.builtins.project.ProjectJkBean;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

@JkDoc("Provides project configuration for generating code from openApi specifications.")
public class OpenapiJkBean extends JkBean {

    @JkDoc("The command line arguments in conjunction with 'exec' method.")
    public String cmdLine;

    @JkDoc("Version of openapi-generator-cli to use.")
    @JkDepSuggest(versionOnly = true, hint = "org.openapitools:openapi-generator:")
    public String cliVersion = JkOpenApiGeneratorCli.DEFAULT_CLI_VERSION;

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

    public OpenapiJkBean() {
        ProjectJkBean projectKBean = this.getBean(ProjectJkBean.class);
        projectKBean.lately(project -> {
            if (autoGenerate) {
                for (String command : commands(this.getRuntime().getProperties())) {
                    project.compilation.addSourceGenerator(new CmdLineGenerator(command));
                }
            }
        });
    }

    public JkOpenApiSourceGenerator addSourceGenerator(JkProject project, String generatorName, String specLocation) {
        JkOpenApiSourceGenerator generator = JkOpenApiSourceGenerator.of(generatorName, specLocation)
                .setCliVersion(this.cliVersion);
        project.compilation.addSourceGenerator(generator);
        return generator;
    }

    public JkOpenApiSourceGenerator addSpringServerSourceGenerator(JkProject project, String specLocation) {
        return addSourceGenerator(project, "spring", specLocation);
    }

    public JkOpenApiSourceGenerator addJavaClientSourceGenerator(JkProject project, String specLocation) {
        return addSourceGenerator(project, "java", specLocation);
    }

    public OpenapiJkBean setCliVersion(String cliVersion) {
        this.cliVersion = cliVersion;
        return this;
    }

    public OpenapiJkBean setAutoGenerate(boolean autoGenerate) {
        this.autoGenerate = autoGenerate;
        return this;
    }

    private int exec(String cmdLine) {
        JkRepoProperties repoProperties = JkRepoProperties.of(this.getRuntime().getProperties());
        JkOpenApiGeneratorCli cmd = JkOpenApiGeneratorCli.of(repoProperties.getDownloadRepos(), cliVersion);
        return cmd.execCmdLine(cmdLine);
    }

    // Source Generator from pure command line. Needed only for command expressed through properties.
    @RequiredArgsConstructor
    private class CmdLineGenerator extends JkSourceGenerator {

        private final String command;

        @Override
        protected String getDirName() {
            return "openapi";
        }

        @Override
        protected void generate(JkProject project, Path generatedSourceDir) {
            JkOpenApiGeneratorCli cmd = JkOpenApiGeneratorCli.of(project.dependencyResolver.getRepos(), cliVersion);
            String effectiveCmdLine = command + " " + JkOpenapiCmdBuilder.OUTPUT_PATH + " " + generatedSourceDir;
            effectiveCmdLine = effectiveCmdLine + " " + JkOpenapiCmdBuilder.ADDITIONAL_PROPERTIES
                    + "=sourceFolder=/";
            effectiveCmdLine = effectiveCmdLine + " --global-property modelTests=false,apiTests=false";
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

    private static List<String> commands(JkProperties properties) {
        return new LinkedList<>(properties.getAllStartingWith("openapi.gen.", false).values());
    }

}
