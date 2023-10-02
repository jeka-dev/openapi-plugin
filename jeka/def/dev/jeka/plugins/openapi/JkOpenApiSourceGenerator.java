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

    private final String generator;

    private final String inputSpecificationLocation;

    private String cliVersion = JkOpenApiGeneratorCmd.DEFAULT_CLI_VERSION;

    private Consumer<GenerateCmdBuilder> customizer = generateCmdBuilder -> {};

    public static JkOpenApiSourceGenerator of(String generator, String specLocation) {
        return new JkOpenApiSourceGenerator(generator, specLocation);
    }

    public static JkOpenApiSourceGenerator ofSpringServer(String specLocation) {
        return new JkOpenApiSourceGenerator("spring", specLocation);
    }

    public static JkOpenApiSourceGenerator ofJavaClient(String specLocation) {
        return new JkOpenApiSourceGenerator("java", specLocation);
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
        GenerateCmdBuilder generateCmdBuilder = GenerateCmdBuilder.of(generator, inputSpecificationLocation);
        generateCmdBuilder.add(GenerateCmdBuilder.OUTPUT_PATH, generatedSourceDir.toString());
        if (JkLog.isVerbose()) {
            generateCmdBuilder.add("--verbose");
        }
        customizer.accept(generateCmdBuilder);
        cmd.exec(generateCmdBuilder.build());
    }

}
