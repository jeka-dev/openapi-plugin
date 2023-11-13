package dev.jeka.plugins.openapi;

import dev.jeka.core.api.depmanagement.JkCoordinateFileProxy;
import dev.jeka.core.api.depmanagement.JkRepoSet;
import dev.jeka.core.api.java.JkJavaProcess;
import dev.jeka.core.api.utils.JkUtilsString;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Provides convenient methods to invoke openApi CLI.
 */
public class JkOpenApiGeneratorCli {

    public static final String DEFAULT_CLI_VERSION = "7.0.1";

    private final JkRepoSet repos;

    private final String cliVersion;

    private JkOpenApiGeneratorCli(JkRepoSet repos, String cliVersion) {
        this.repos = repos;
        this.cliVersion = cliVersion;
    }

    public final static JkOpenApiGeneratorCli of(JkRepoSet repoSet, String cliVersion) {
        return new JkOpenApiGeneratorCli(repoSet, cliVersion);
    }

    public int exec(String ...args) {
        return javaProcess().addParams(args).exec();
    }

    public int exec(List<String> args) {
        return exec(args.toArray(new String[0]));
    }

    /**
     * Executes the specified command line as <li>generate -g go --additional-properties=prependFormOrBodyParameters=true -o out -i petstore.yaml</li>
     */
    public int execCmdLine(String args) {
        return exec(Arrays.asList(JkUtilsString.translateCommandline(args)));
    }

    private JkJavaProcess javaProcess() {
        return JkJavaProcess.ofJavaJar(cliJar(), null)
                .setLogCommand(true)
                .setLogOutput(true);
    }

    private Path cliJar() {
        return JkCoordinateFileProxy.of(repos, "org.openapitools:openapi-generator-cli:"
                + cliVersion).get();
    }


}
