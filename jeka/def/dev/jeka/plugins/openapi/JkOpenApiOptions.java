package dev.jeka.plugins.openapi;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JkOpenApiOptions {

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
}
