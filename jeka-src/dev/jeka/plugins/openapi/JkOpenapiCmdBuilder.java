package dev.jeka.plugins.openapi;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Builder for constructing an openAPi 'generate' command line.
 * See <a href="https://openapi-generator.tech/docs/usage#generate">documentation</a>.
 *
 * This class is meant to be used in conjunction with {@link JkOpenApiGeneratorCli},
 * {@link JkOpenApiSourceGenerator} or {@link OpenapiKBean}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JkOpenapiCmdBuilder {

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

    private List<String> args = new LinkedList();

    private StringBuilder additionalProperties = new StringBuilder();

    private StringBuilder globalProperties = new StringBuilder();

    private StringBuilder importMappings = new StringBuilder();

    private StringBuilder typeMappings = new StringBuilder();

    private boolean generateTests = false;

    public static JkOpenapiCmdBuilder of(String generatorName, String specLocation) {
        JkOpenapiCmdBuilder builder = new JkOpenapiCmdBuilder();
        builder.add(GENERATOR_NAME, generatorName);
        builder.add(INPUT_SPEC, specLocation);
        return builder;
    }

    public JkOpenapiCmdBuilder add(String ...args) {
        Arrays.stream(args).forEach(item -> this.args.add(item));
        return this;
    }

    public JkOpenapiCmdBuilder addApiAndModelPackage(String packageName) {
        return add(API_PACKAGE, packageName).add(MODEL_PACKAGE, packageName);
    }



    /**
     * Adds additional property specific to the generator. See <a href="https://openapi-generator.tech/docs/generators">documentation</a>.
     */
    public JkOpenapiCmdBuilder addAdditionalProperties(String key, String value) {
        if (additionalProperties.length() > 0) {
            additionalProperties.append(",");
        }
        additionalProperties.append(key).append("=").append(value);
        return  this;
    }

    /**
     * Adds global property specific to the generator.
     * See <a href="https://openapi-generator.tech/docs/globals">documentation</a>.
     */
    public JkOpenapiCmdBuilder addGlobalProperties(String key, String value) {
        if (globalProperties.length() > 0) {
            globalProperties.append(",");
        }
        globalProperties.append(key).append("=").append(value);
        return  this;
    }

    /**
     * Adds import-mapping. See <a href="https://openapi-generator.tech/docs/usage#generate">documentation</a>.
     */
    public JkOpenapiCmdBuilder addImportMapping(String primitiveTypeName, String fullQualifiedClassName) {
        if (importMappings.length() > 0) {
            importMappings.append(",");
        }
        importMappings.append(primitiveTypeName).append("=").append(fullQualifiedClassName);
        return  this;
    }

    /**
     * Adds type-mapping. See <a href="https://openapi-generator.tech/docs/usage#generate">documentation</a>.
     */
    public JkOpenapiCmdBuilder addTypeMapping(String primitiveTypeName, String fullQualifiedClassName) {
        if (typeMappings.length() > 0) {
            typeMappings.append(",");
        }
        typeMappings.append(primitiveTypeName).append("=").append(fullQualifiedClassName);
        return  this;
    }

    /**
     * Returns the arguments that forms the 'generate' command to execute.
     */
    public List<String> build() {
        LinkedList result = new LinkedList<>();
        result.add("generate");
        result.addAll(this.args);
        if (additionalProperties.length() > 0) {
            result.add(ADDITIONAL_PROPERTIES + "=" + additionalProperties);
        }
        if (globalProperties.length() > 0) {
            result.add(GLOBAL_PROPERTY + "=" + globalProperties);
        }
        if (importMappings.length() > 0) {
            result.add(IMPORT_MAPPINGS + "=" + importMappings);
        }
        if (typeMappings.length() > 0) {
            result.add(TYPE_MAPPINGS + "=" + typeMappings);
        }
        result.add(ADDITIONAL_PROPERTIES + "=sourceFolder=/");
        result.add(GLOBAL_PROPERTY);
        result.add("modelTests=false,apiTests=false");
        return result;
    }
}
