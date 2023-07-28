package dev.jeka.plugins.openapi;

import dev.jeka.core.api.depmanagement.JkRepoProperties;
import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.api.utils.JkUtilsString;
import dev.jeka.core.tool.JkBean;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.builtins.project.ProjectJkBean;

import java.util.Map;
import java.util.function.Consumer;

@JkDoc("Provides project configuration for generating code from openApi specifications.")
public class OpenApiJkBean extends JkBean {

    @JkDoc("The command line arguments in conjonction with 'exec' method.")
    public String cmdArgs;

    @JkDoc("Relative path from project root, or url, to the openapi specification file.")
    public String definitionFile;

    @JkDoc("Version of openapi-generator-cli to use.")
    public String jarVersion = "6.2.1";

    @JkDoc("Degault package name for both model and api.")
    public String packageName = "org.openapitools";

    private Consumer<JkOpenApiSourceGenerator> configurer = gen -> {};

    OpenApiJkBean() {
        this.getRuntime().getBeanOptional(ProjectJkBean.class).ifPresent(projectBean -> {
            projectBean.configure(this::configure);
        });
    }

    /**
     * Returns a conf
     */
    public JkOpenApiSourceGenerator getSourceGenerator() {
        JkOpenApiSourceGenerator result = new JkOpenApiSourceGenerator(jarVersion)
                .setInputSpec(definitionFile);
        if (packageName != null) {
            String name = packageName.trim();
            result
                    .addArguments(JkOpenApiOptions.API_PACKAGE, name)
                    .addArguments(JkOpenApiOptions.MODEL_PACKAGE, name)
                    .addArguments(JkOpenApiOptions.INVOKER_PACKAGE, name)
                    .addArguments(JkOpenApiOptions.PACKAGE_NAME, name);
        }
        //result.setGeneratorJava();
        result.addArguments(JkUtilsString.translateCommandline(cmdArgs));
        options().entrySet().stream()
                        .filter(entry -> entry.getKey().startsWith("-"))
                        .forEach(entry -> result.addArguments(entry.getKey(), entry.getValue()));

        configurer.accept(result);
        return result;
    }

    public OpenApiJkBean configure(Consumer<JkOpenApiSourceGenerator> consumer) {
        this.configurer = consumer;
        return this;
    }

    @JkDoc("Execute openApi cli with argument specified in 'cmdArgs'.")
    public void exec() {
        cmd().arguments(cmdArgs).exec();
    }

    public void cliHelp() {
        cmd().arguments("help").exec();
    }

    public void cliHelpGenerate() {
        cmd().argumentLine("help generate").exec();
    }

    @JkDoc("Display the available generators")
    public void cliList() {
        cmd().arguments("list").exec();
    }

    @JkDoc("Display config-help for spring server")
    public void cliConfigHelpSpring() {
        cmd().arguments("config-help", "-g", "spring").exec();
    }

    public Map<String, String> options() {
        return this.getRuntime().getProperties().getAllStartingWith("openapi.", false);
    }

    private JkOpenApiGeneratorCmd cmd() {
        JkRepoProperties repoProperties = JkRepoProperties.of(this.getRuntime().getProperties());
        return JkOpenApiGeneratorCmd.of(repoProperties.getDownloadRepos(), jarVersion);
    }

    private void configure(JkProject project) {
        project.compilation.addSourceGenerator(this.getSourceGenerator());
    }

}
