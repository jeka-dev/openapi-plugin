package dev.jeka.plugins.openapi;

import dev.jeka.core.api.depmanagement.JkCoordinateFileProxy;
import dev.jeka.core.api.depmanagement.JkRepoSet;
import dev.jeka.core.api.java.JkJavaProcess;
import dev.jeka.core.api.utils.JkUtilsString;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class JkOpenApiGeneratorCmd {

    public static final String DEFAULT_CLI_VERSION = "6.2.1";

    private JkRepoSet repos;

    private String openapiGeneratorCliVersion;

    private List<String> arguments = new LinkedList<>();

    private JkOpenApiGeneratorCmd(JkRepoSet repos, String openapiGeneratorCliVersion) {
        this.repos = repos;
        this.openapiGeneratorCliVersion = openapiGeneratorCliVersion;
    }

    public final static JkOpenApiGeneratorCmd of(JkRepoSet repoSet, String version) {
        return new JkOpenApiGeneratorCmd(repoSet, version);
    }

    /**
     * Add the 'generate' argument
     */
    public JkOpenApiGeneratorCmd generateCmd() {
        arguments.add("generate");
        return this;
    }

    /**
     * The specification file that contains open API specification from which we want to generate code.
     * @param fileOrUrl can be an url or a file path.
     */
    public JkOpenApiGeneratorCmd specificationFile(String fileOrUrl) {
        return argumentLine(JkOpenApiOptions.INPUT_SPEC + fileOrUrl);
    }

    /**
     * Thegenerator used to generate code (e.g. java)
     */
    public JkOpenApiGeneratorCmd generator(String generatorName) {
        return argumentLine(JkOpenApiOptions.GENERATOR_NAME + generatorName);
    }

    public JkOpenApiGeneratorCmd outputDirectory(Path path) {
        return argumentLine(JkOpenApiOptions.OUTPUT_PATH + path.toString());
    }

    /**
     * Add arguments expressed in a single line, space separated arguments.
     */
    public JkOpenApiGeneratorCmd argumentLine(String args) {
        arguments.addAll(Arrays.asList(JkUtilsString.translateCommandline(args)));
        return this;
    }

    /**
     * Add arguments
     */
    public JkOpenApiGeneratorCmd arguments(String... args) {
        arguments.addAll(Arrays.asList(args));
        return this;
    }

    /**
     * Add arguments
     */
    public JkOpenApiGeneratorCmd arguments(List<String> args) {
        arguments.addAll(args);
        return this;
    }

    public void exec() {
        javaProcess().addParams(arguments).exec();
    }

    private JkJavaProcess javaProcess() {
        return JkJavaProcess.ofJavaJar(cliJar(), null)
                .setLogCommand(true)
                .setLogOutput(true);
    }

    private Path cliJar() {
        return JkCoordinateFileProxy.of(repos, "org.openapitools:openapi-generator-cli:"
                + openapiGeneratorCliVersion).get();
    }


}
