package dev.jeka.plugins.openapi;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Builder for constructing an openAPi 'generate' command line programmatically.
 * See <a href="https://openapi-generator.tech/docs/usage#generate">documentation</a>.
 * <p>
 * This class is meant to be used in conjunction with {@link JkOpenApiGeneratorCli},
 * {@link JkOpenApiSourceGenerator} or {@link OpenapiKBean}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JkOpenapiGenerateBuilder {

    public static final String INPUT_SPEC = "--input-spec";

    public static final String GENERATOR_NAME = "--generator-name";

    public static final String OUTPUT_PATH = "--output";

    public static final String MODEL_NAME_PREFIX = "--model-name-prefix";

    public static final String MODEL_NAME_SUFFIX = "--model-name-suffix";

    public static final String MODEL_PACKAGE = "--model-package";

    public static final String PACKAGE_NAME = "--package-name";

    public static final String INVOKER_PACKAGE = "--invoker-package";

    public static final String API_NAME_SUFFIX = "--api-name-suffix";

    public static final String API_PACKAGE = "--api-package";

    public static final String BASE_PACKAGE = "--base-package";

    public static final String ADDITIONAL_PROPERTIES = "--additional-properties";

    public static final String IMPORT_MAPPINGS = "--import-mappings";

    public static final String TYPE_MAPPINGS = "--type-mappings";

    public static final String GLOBAL_PROPERTY = "--global-property";

    private final List<String> args = new LinkedList<>();

    private final StringBuilder additionalProperties = new StringBuilder();

    private final StringBuilder globalProperties = new StringBuilder();

    private final StringBuilder importMappings = new StringBuilder();

    private final StringBuilder typeMappings = new StringBuilder();

    private boolean generateTests = false;

    public static JkOpenapiGenerateBuilder of() {
        return new JkOpenapiGenerateBuilder();
    }

    public static JkOpenapiGenerateBuilder of(String generatorName, String specLocation) {
        JkOpenapiGenerateBuilder builder = new JkOpenapiGenerateBuilder();
        builder.add(GENERATOR_NAME, generatorName);
        builder.add(INPUT_SPEC, specLocation);
        return builder;
    }

    /**
     * Creates a deep copy of the current {@code JkOpenapiCmdBuilder} instance.
     * The returned instance contains the same configurations and properties
     * as the original object but is a separate object that can be modified independently.
     */
    public JkOpenapiGenerateBuilder copy() {
        JkOpenapiGenerateBuilder result = new JkOpenapiGenerateBuilder();
        result.args.addAll(args);
        result.globalProperties.append(globalProperties);
        result.importMappings.append(importMappings);
        result.typeMappings.append(typeMappings);
        result.additionalProperties.append(additionalProperties);
        result.typeMappings.append(typeMappings);
        result.generateTests = generateTests;
        return result;
    }

    public JkOpenapiGenerateBuilder add(String ...args) {
        this.args.addAll(Arrays.asList(args));
        return this;
    }

    public JkOpenapiGenerateBuilder addApiAndModelPackage(String packageName) {
        return add(API_PACKAGE, packageName).add(MODEL_PACKAGE, packageName);
    }

    /**
     * Adds additional property specific to the generator. See <a href="https://openapi-generator.tech/docs/generators">documentation</a>.
     */
    public JkOpenapiGenerateBuilder addAdditionalProperties(String key, String value) {
        if (!additionalProperties.isEmpty()) {
            additionalProperties.append(",");
        }
        additionalProperties.append(key).append("=").append(value);
        return  this;
    }

    /**
     * Adds global property specific to the generator.
     * See <a href="https://openapi-generator.tech/docs/globals">documentation</a>.
     */
    public JkOpenapiGenerateBuilder addGlobalProperties(String key, String value) {
        if (!globalProperties.isEmpty()) {
            globalProperties.append(",");
        }
        globalProperties.append(key).append("=").append(value);
        return  this;
    }

    /**
     * Adds import-mapping. See <a href="https://openapi-generator.tech/docs/usage#generate">documentation</a>.
     */
    public JkOpenapiGenerateBuilder addImportMapping(String primitiveTypeName, String fullQualifiedClassName) {
        if (!importMappings.isEmpty()) {
            importMappings.append(",");
        }
        importMappings.append(primitiveTypeName).append("=").append(fullQualifiedClassName);
        return  this;
    }

    /**
     * Adds type-mapping. See <a href="https://openapi-generator.tech/docs/usage#generate">documentation</a>.
     */
    public JkOpenapiGenerateBuilder addTypeMapping(String primitiveTypeName, String fullQualifiedClassName) {
        if (!typeMappings.isEmpty()) {
            typeMappings.append(",");
        }
        typeMappings.append(primitiveTypeName).append("=").append(fullQualifiedClassName);
        return this;
    }

    /**
     * Sets the name of the generator to be used for code generation.
     *
     * @param generatorName The name of the generator. It specifies the code generator to use.
     */
    public JkOpenapiGenerateBuilder setGeneratorName(String generatorName) {
        return setStartingOption(GENERATOR_NAME, generatorName);
    }

    /**
     * Sets the input specification for the OpenAPI code generation process.
     *
     * @param specLocation The location of the OpenAPI specification. It can be a URL or a file path.
     */
    public JkOpenapiGenerateBuilder setInputSpec(String specLocation) {
        return setStartingOption(INPUT_SPEC, specLocation);
    }

    /**
     * Returns the arguments that forms the 'generate' command to execute.
     */
    public List<String> build() {
        LinkedList<String> result = new LinkedList<>();
        result.add("generate");
        result.addAll(this.args);
        if (!additionalProperties.isEmpty()) {
            result.add(ADDITIONAL_PROPERTIES + "=" + additionalProperties);
        }
        if (!globalProperties.isEmpty()) {
            result.add(GLOBAL_PROPERTY + "=" + globalProperties);
        }
        if (!importMappings.isEmpty()) {
            result.add(IMPORT_MAPPINGS + "=" + importMappings);
        }
        if (!typeMappings.isEmpty()) {
            result.add(TYPE_MAPPINGS + "=" + typeMappings);
        }
        result.add(ADDITIONAL_PROPERTIES + "=sourceFolder=/");
        result.add(GLOBAL_PROPERTY);
        result.add("modelTests=false,apiTests=false");
        return result;
    }

    private JkOpenapiGenerateBuilder setStartingOption(String optionName, String optionValue) {
        int optionIndex = this.args.indexOf(optionName);
        if (optionIndex < 0) {
            this.args.add(0, optionName);
            this.args.add(1, optionValue);
        } else {
            int valueIndex = optionIndex + 1;
            this.args.remove(valueIndex);
            this.args.add(valueIndex, optionValue);
        }
        return this;
    }
}
