package dev.jeka.plugins.openapi;

import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.api.project.JkSourceGenerator;
import dev.jeka.core.api.system.JkLog;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JkOpenApiSourceGenerator extends JkSourceGenerator {

    private final String generatorName;

    private final String inputSpecLocation;

    private String cliVersion = JkOpenApiGeneratorCmd.DEFAULT_CLI_VERSION;

    private Consumer<GenerateCmdBuilder> customizer = generateCmdBuilder -> {};

    public static JkOpenApiSourceGenerator of(String generatorName, String specLocation) {
        return new JkOpenApiSourceGenerator(generatorName, specLocation);
    }

    public JkOpenApiSourceGenerator setCliVersion(String cliVersion) {
        this.cliVersion = cliVersion;
        return this;
    }

    public JkOpenApiSourceGenerator customize(Consumer<GenerateCmdBuilder> customizer) {
        this.customizer = customizer;
        return this;
    }

    @Override
    public String getDirName() {
        return "openapi";
    }

    @Override
    protected void generate(JkProject project, Path generatedSourceDir) {
        JkOpenApiGeneratorCmd cmd = JkOpenApiGeneratorCmd.of(project.dependencyResolver.getRepos(), cliVersion);
        GenerateCmdBuilder generateCmdBuilder = GenerateCmdBuilder.of(generatorName, inputSpecLocation);
        generateCmdBuilder.add(GenerateCmdBuilder.OUTPUT_PATH, generatedSourceDir.toString());
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
