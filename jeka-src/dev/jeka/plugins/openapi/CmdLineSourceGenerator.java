package dev.jeka.plugins.openapi;

import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.api.project.JkProjectSourceGenerator;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.utils.JkUtilsString;

import java.nio.file.Path;
import java.util.List;

// Source Generator from pure command line. Needed only for command expressed through properties.
class CmdLineSourceGenerator implements JkProjectSourceGenerator {

    private final String name;

    private final String command;

    private final String cliVersion;

    CmdLineSourceGenerator(String name, String cliVersion, String command) {
        this.command = command;
        this.cliVersion = cliVersion;
        this.name = name;
    }

    @Override
    public String getDirName() {
        return "openapi";
    }

    @Override
    public void generate(JkProject project, Path generatedSourceDir) {
        JkLog.info("Generating OpenApi " + name);
        JkOpenApiGeneratorCli cmd = JkOpenApiGeneratorCli.of(project.dependencyResolver.getRepos(), cliVersion);
        String effectiveCmdLine = command;
        List<String> args = JkUtilsString.parseCommandlineAsList(command);
        if (!args.contains("--output")) {
            effectiveCmdLine = effectiveCmdLine  + " " + JkOpenapiGenerateBuilder.OUTPUT_PATH + " " + generatedSourceDir;
        }
        effectiveCmdLine = effectiveCmdLine + " " + JkOpenapiGenerateBuilder.ADDITIONAL_PROPERTIES
                + "=sourceFolder=/";
        //effectiveCmdLine = effectiveCmdLine + " --global-property modelTests=false,apiTests=false";
        if (JkLog.isDebug() && !args.contains("--verbose")) {
            effectiveCmdLine = effectiveCmdLine + " --verbose";
        }
        cmd.execCmdLine(effectiveCmdLine);

        // Keep only java sources
        JkPathTree.of(generatedSourceDir).andMatching(false, "**/*java").deleteContent();
    }

    @Override
    public String toString() {
        return "OpenapiKBeanGenerator";
    }

}
