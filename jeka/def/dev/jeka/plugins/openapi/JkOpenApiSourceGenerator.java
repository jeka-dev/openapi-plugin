package dev.jeka.plugins.openapi;

import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.api.project.JkSourceGenerator;
import lombok.RequiredArgsConstructor;


import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
public class JkOpenApiSourceGenerator extends JkSourceGenerator {

    private final String cliVersion;

    private List<String> arguments = new LinkedList<>();

    public JkOpenApiSourceGenerator setInputSpec(String pathOrUrl) {
        return addArguments(JkOpenApiOptions.INPUT_SPEC, pathOrUrl);
    }

    public JkOpenApiSourceGenerator setGenerator(String generatorName) {
        return addArguments(JkOpenApiOptions.GENERATOR_NAME, generatorName);
    }

    public JkOpenApiSourceGenerator setGeneratorJava() {
        return setGenerator("java");
    }

    public JkOpenApiSourceGenerator addArguments(String... args) {
        arguments.addAll(Arrays.asList(args));
        return this;
    }

    @Override
    public String getDirName() {
        return "openapi";
    }

    @Override
    protected void generate(JkProject project, Path generatedSourceDir) {
        JkOpenApiGeneratorCmd.of(project.dependencyResolver.getRepos(), cliVersion)
                .generateCmd()
                .arguments(JkOpenApiOptions.OUTPUT_PATH, generatedSourceDir.toString())
                .arguments(arguments)
                .exec();
    }

}
