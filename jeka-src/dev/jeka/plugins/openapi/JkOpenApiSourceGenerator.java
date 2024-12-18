package dev.jeka.plugins.openapi;

import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.api.project.JkProjectSourceGenerator;
import dev.jeka.core.api.system.JkLog;

import java.nio.file.Path;

/**
 * A {@link JkProjectSourceGenerator} to be added to projects needing an openApi source
 * generation.
 */
public final class JkOpenApiSourceGenerator implements JkProjectSourceGenerator {

    private String cliVersion = JkOpenApiGeneratorCli.DEFAULT_CLI_VERSION;

    /**
     * Represents a pre-configured instance of {@link JkOpenapiCmdBuilder}
     * designed to facilitate the construction of OpenAPI 'generate' command-line arguments.
     */
    public final JkOpenapiCmdBuilder openapiCmd = JkOpenapiCmdBuilder.of();

    private JkOpenApiSourceGenerator(String generatorName, String inputSpecLocation) {
        openapiCmd.setInputSpec(inputSpecLocation);
        openapiCmd.setGeneratorName(generatorName);
    }

    /**
     * Creates a {@link JkProjectSourceGenerator} instance, specifying the generator to use, and
     * the specification location.
     * @param generatorName on generator selected from <a href="https://openapi-generator.tech/docs/generators">this list</a>
     * @param specLocation a file path or an url
     */
    public static JkOpenApiSourceGenerator of(String generatorName, String specLocation) {
        return new JkOpenApiSourceGenerator(generatorName, specLocation);
    }

    /**
     * Creates a {@link JkProjectSourceGenerator} instance for generating spring server code.
     * @param specLocation a file path or an url
     */
    public static JkOpenApiSourceGenerator ofSpringServer(String specLocation) {
        return new JkOpenApiSourceGenerator("spring", specLocation);
    }

    /**
     * Creates a {@link JkProjectSourceGenerator} instance for generating Java client code.
     * @param specLocation a file path or an url
     */
    public static JkOpenApiSourceGenerator ofJavaClient(String specLocation) {
        return new JkOpenApiSourceGenerator("java", specLocation);
    }

    /**
     * Sets the OpenApi generator cli version to invoke.
     */
    public JkOpenApiSourceGenerator setCliVersion(String cliVersion) {
        this.cliVersion = cliVersion;
        return this;
    }

    @Override
    public String getDirName() {
        return "openapi";
    }

    @Override
    public void generate(JkProject project, Path generatedSourceDir) {
        JkOpenApiGeneratorCli cli = JkOpenApiGeneratorCli.of(project.dependencyResolver.getRepos(), cliVersion);
        JkOpenapiCmdBuilder cmd = openapiCmd.copy()
                .add(JkOpenapiCmdBuilder.OUTPUT_PATH, generatedSourceDir.toString())
                .addAdditionalProperties("sourceFolder", "/")
                .addGlobalProperties("modelTests", "false")
                .addGlobalProperties("apiTests", "false");
        if (JkLog.isVerbose()) {
            cmd.add("--verbose");
        }
        cli.exec(cmd.build());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + cliVersion;
    }

}
