package dev.jeka.plugins.openapi;

import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.api.project.JkSourceGenerator;
import dev.jeka.core.api.system.JkLog;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * A {@link JkSourceGenerator} to be added to projects needing an openApi source
 * generation.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JkOpenApiSourceGenerator extends JkSourceGenerator {

    private final String generatorName;

    private final String inputSpecLocation;

    private String cliVersion = JkOpenApiGeneratorCli.DEFAULT_CLI_VERSION;

    private Consumer<JkOpenapiCmdBuilder> customizer = generateCmdBuilder -> {};

    /**
     * Creates a {@link JkSourceGenerator} instance, specifying the generator to use, and
     * the specification location.
     * @param generatorName on generator selected from <a href="https://openapi-generator.tech/docs/generators">this list</a>
     * @param specLocation a file path or an url
     */
    public static JkOpenApiSourceGenerator of(String generatorName, String specLocation) {
        return new JkOpenApiSourceGenerator(generatorName, specLocation);
    }

    /**
     * Creates a {@link JkSourceGenerator} instance for generating spring server code.
     * @param specLocation a file path or an url
     */
    public static JkOpenApiSourceGenerator ofSpringServer(String specLocation) {
        return new JkOpenApiSourceGenerator("spring", specLocation);
    }

    /**
     * Creates a {@link JkSourceGenerator} instance for generating Java client code.
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

    /**
     * Defines the openApi command line to be executed for generating sources. There is no
     * need to specify the specification location here, as it has already been mentioned
     * through the {@link #of(String, String)} factory method.
     * @param customizer A builder to construct command line conveniently.
     */
    public JkOpenApiSourceGenerator customize(Consumer<JkOpenapiCmdBuilder> customizer) {
        this.customizer = customizer;
        return this;
    }

    @Override
    public String getDirName() {
        return "openapi";
    }

    @Override
    protected void generate(JkProject project, Path generatedSourceDir) {
        JkOpenApiGeneratorCli cmd = JkOpenApiGeneratorCli.of(project.dependencyResolver.getRepos(), cliVersion);
        JkOpenapiCmdBuilder generateCmdBuilder = JkOpenapiCmdBuilder.of(generatorName, inputSpecLocation)
                .add(JkOpenapiCmdBuilder.OUTPUT_PATH, generatedSourceDir.toString())
                .addAdditionalProperties("sourceFolder", "/")
                .addGlobalProperties("modelTests", "false")
                .addGlobalProperties("apiTests", "false");
        if (JkLog.isVerbose()) {
            generateCmdBuilder.add("--verbose");
        }
        customizer.accept(generateCmdBuilder);
        cmd.exec(generateCmdBuilder.build());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + cliVersion;
    }

}
