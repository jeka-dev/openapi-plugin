package dev.jeka.plugins.openapi;

import dev.jeka.core.api.depmanagement.JkDepSuggest;
import dev.jeka.core.api.project.JkProject;

public class JkOpenApi {

    private final String version;

    private JkOpenApi(String version) {
        this.version = version;
    }

    /**
     * Creates a {@link JkOpenApi} using the specified openApi cli version.
     */
    public static JkOpenApi ofVersion(@JkDepSuggest(versionOnly = true, hint = "org.openapitools:openapi-generator-cli:") String version) {
        return new JkOpenApi(version);
    }

    /**
     * Creates a {@link JkOpenApi} using the default openApi cli version.
     */
    public static JkOpenApi ofDefaultVersion() {
        return ofVersion(JkOpenApiGeneratorCli.DEFAULT_CLI_VERSION);
    }

    /**
     * Appends a source generator to the specified project, generating code using the specified generatorName and
     * the specified location for specification. <p>
     * This returns a {@link JkOpenApiSourceGenerator} that can be customized.
     *
     *
     * @param generatorName The code generator to use. See <a href="https://openapi-generator.tech/docs/generators">list here</a>.
     * @param specLocation The url or local path of the specification location.
     */
    public JkOpenApiSourceGenerator addSourceGenerator(JkProject project, String generatorName, String specLocation) {
        JkOpenApiSourceGenerator generator = JkOpenApiSourceGenerator.of(generatorName, specLocation)
                .setCliVersion(version);
        project.compilation.addSourceGenerator(generator);
        return generator;
    }

    /**
     * @see #addSourceGenerator(JkProject, String, String)
     * @param packageName the package name where source code should be generated
     */
    public JkOpenApiSourceGenerator addSourceGenerator(JkProject project, String generatorName, String specLocation, String packageName) {
        JkOpenApiSourceGenerator generator = JkOpenApiSourceGenerator.of(generatorName, specLocation)
                .setCliVersion(version);
        generator.openapiCmd
                        .addApiAndModelPackage(packageName)
                        .add(JkOpenapiCmdBuilder.MODEL_NAME_PREFIX, "Rest");
        project.compilation.addSourceGenerator(generator);
        return generator;
    }

    /**
     * Appends a Springboot server code generation to the specified project.
     * The generated model code will bbe prefixed with 'Rest'.
     * @see #addSourceGenerator(JkProject, String, String)
     */
    public JkOpenApiSourceGenerator addSpringbootServerGenerator(JkProject project, String specLocation, String packageName) {
        JkOpenApiSourceGenerator result = addSourceGenerator(project, "spring", specLocation, packageName);
        result.openapiCmd.addAdditionalProperties("useSpringBoot3", "true");
        return result;
    }

    /**
     * Appends a Java client code generation to the specified project.
     * The generated model code will bbe prefixed with 'Rest'.
     * @see #addSourceGenerator(JkProject, String, String)
     */
    public JkOpenApiSourceGenerator addJavaGenerator(JkProject project, String specLocation, String packageName) {
        return addSourceGenerator(project, "client", specLocation, packageName);
    }

}
