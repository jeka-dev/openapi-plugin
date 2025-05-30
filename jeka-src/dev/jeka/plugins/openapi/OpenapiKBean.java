package dev.jeka.plugins.openapi;

import dev.jeka.core.api.depmanagement.JkDepSuggest;
import dev.jeka.core.api.depmanagement.JkRepoProperties;
import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.utils.JkUtilsString;
import dev.jeka.core.tool.*;
import dev.jeka.core.tool.builtins.project.ProjectKBean;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

@JkDoc("Provides project configuration for generating code from openApi specifications.")
public class OpenapiKBean extends KBean {

    @JkDoc("The command line arguments in conjunction with 'exec' method.")
    @JkSuggest({"generate -g spring ", "generate -g java "})
    public String cmdLine;

    @JkDoc("Version of openapi-generator-cli to use.")
    @JkDepSuggest(versionOnly = true, hint = "org.openapitools:openapi-generator:")
    public String cliVersion = JkOpenApiGeneratorCli.DEFAULT_CLI_VERSION;

    @JkDoc("The generator pass in parameter to the 'helpGenerator()' method.")
    @JkSuggest({"java", "spring", "java-micronaut", "java-spring", "jaxrs", "java-vertx"})
    public String helpGenerator = "java";

    @JkDoc("If true, the specified cmdLine will be run to generate sources at compile time")
    public boolean configureProject = true;

    @JkDoc("The generation configuration container, keys can be anything meaningful for you ar just digits as 0,1,...")
    @JkSuggest({"0", "1", "2"})
    public final GeneratorOption gen = new GeneratorOption();

    @JkDoc("Executes openApi cli with argument specified in 'cmdArgs'.")
    public void exec() {
        exec(this.cmdLine, true);
    }

    @JkDoc("Displays generic help about openApi cli options")
    public void helpCli() {
        exec("help", true);
    }

    @JkDoc("Displays help about available for 'generate' options")
    public void helpGenerate() {
        exec("help generate", true);
    }

    @JkDoc("Displays the available generators")
    public void helpListGenerators() {
        exec("list", true);
    }

    @JkDoc("Displays config-help for spring server")
    public void helpGenerator() {
        String msg = "Generator: " + this.helpGenerator;
        System.out.println(JkUtilsString.repeat("_", msg.length()));
        System.out.println("Generator: " + this.helpGenerator);
        System.out.print(JkUtilsString.repeat("_", msg.length()));

        exec("config-help -g " + this.helpGenerator, true);
    }

    @JkDoc("Runs all defined generators")
    public void generate() {
        JkProject project = load(ProjectKBean.class).project;
        gen.cmdLine.getKeys().forEach(key -> gen(project, key, gen.cmdLine.get(key)));
        gen.config.getKeys().forEach(key -> gen(project, key, gen.config.get(key).toCmdLine()));
    }

    @JkPostInit
    private void postInit(ProjectKBean projectKBean) {
        if (configureProject) {
            for (String key : gen.cmdLine.getKeys()) {
                JkLog.verbose("Register openApi generator command line %s", key);
                String cmdLine = gen.cmdLine.get(key);
                projectKBean.project.compilation.addSourceGenerator(new CmdLineSourceGenerator(key, cliVersion, cmdLine));
            }
            for (String key : gen.config.getKeys()) {
                JkLog.verbose("Register openApi generator config %s", key);
                Config config = gen.config.get(key);
                projectKBean.project.compilation.addSourceGenerator(new CmdLineSourceGenerator(key, cliVersion,
                        config.toCmdLine()));
            }
        }
    }

    @JkDoc
    public static final class GeneratorOption {

        @JkDoc("OpenApi command line to execute.")
        @JkSuggest(value = {"0", "1", "2"}, multiValues = {"generate -g spring", "generate -g java"})
        public final JkMultiValue<String> cmdLine = JkMultiValue.of(String.class);

        @JkDoc("OpenApi command line to execute.")
        @JkSuggest({"0", "1", "2"})
        public final JkMultiValue<Config> config = JkMultiValue.of(Config.class);

    }

    private void gen(JkProject project, String key, String cmdLine) {
        new CmdLineSourceGenerator(key, cliVersion, cmdLine).generate(project,
                this.getOutputDir().resolve("generated-sources/java/openapi"));
    }

    private int exec(String cmdLine, boolean printOutPut) {
        JkRepoProperties repoProperties = JkRepoProperties.of(this.getRunbase().getProperties());
        JkOpenApiGeneratorCli cmd = JkOpenApiGeneratorCli.of(repoProperties.getDownloadRepos(), cliVersion)
                .setPrintOutput(printOutPut);
        return cmd.execCmdLine(cmdLine);
    }

    public static class Config {

        @JkDoc("The options as mentioned in 'helpGeneral")
        @JkSuggest({
                "authorization",
                "api-name-suffix",
                "api-package",
                "artifact-id",
                "artifact-version",
                "configuration",
                "dry-run",
                "engine",
                "enable-post-process-file",
                "generate-alias-as-model",
                "http-user-agent",
                "ignore-file-override",
                "import-mappings",
                "input-spec-root-directory",
                "invoker-package",
                "language-specific-primitives",
                "legacy-discriminator-behavior",
                "library",
                "merged-spec-filename",
                "minimal-update",
                "model-name-prefix",
                "model-name-suffix",
                "model-package",
                "openapi-generator-ignore-list",
                "package-name",
                "release-note",
                "remove-operation-id-prefix",
                "reserved-words-mappings",
                "skip-overwrite",
                "server-variables",
                "skip-operation-example",
                "skip-validate-spec",
                "strict-spec",
                "template-dir",
                "verbose"
        })
        public JkMultiValue<String> options = JkMultiValue.of(String.class);

        @JkDoc("Path or URL to the OpenAPI specification file used for code generation")
        public String inputSpec;

        @JkSuggest({"java", "spring", "java-micronaut", "java-spring", "jaxrs", "java-vertx"})
        public String generatorName;

        @JkDoc("Package into generate invoker, api and model")
        public String generationPackage;

        public boolean generateTest;

        @JkDoc("Use it only if you don't want to generate the code at the standard location.")
        public Path outputDir;

        @JkSuggest({"string", "date", "integer", "number", "array"})
        public final JkMultiValue<String> typeMappings = JkMultiValue.of(String.class);

        @JkDoc("Specifies mappings between the model name and the new name as 'model_name=AnotherName'")
        public final JkMultiValue<String> modelNameMappings = JkMultiValue.of(String.class);

        @JkDoc("Specifies mappings between the property name and the new name in the format of 'prop_name=PropName'")
        public final JkMultiValue<String> nameMappings = JkMultiValue.of(String.class);

        @JkDoc("Specifies mappings between the operation id name and the new name in the format of " +
                "'operation_id_name=AnotherName'")
        public final JkMultiValue<String> operationIdNameMappings = JkMultiValue.of(String.class);

        @JkDoc("Specifies mappings between the parameter name and the new name in the format of param_name=paramName")
        public final JkMultiValue<String> parameterNameMappings = JkMultiValue.of(String.class);

        @JkDoc("Specifies mappings between the schema and the new name in the format of schema_a=Cat")
        public final JkMultiValue<String> schemaMappings = JkMultiValue.of(String.class);

        @JkDoc("The values to pass to --global-properties options. Leave an empty value after '=' to skip the '=' value.")
        @JkSuggest({
                "models",
                "apis",
                "supportingFiles",
                "modelTests",
                "modelDocs",
                "apiTests",
                "apiDocs",
                "skipFormModel",
                "excludeSchemas",
                "verbose"
        })
        public final JkMultiValue<String> globalProperties = JkMultiValue.of(String.class);

        @JkDoc("Generator specific options to pass to --additional-properties, as mentioned in 'helpGenerator'")
        @JkSuggest({
                "apiFirst",
                "booleanGetterPrefix",
                "dateLibrary",
                "useBeanValidation",
                "performBeanValidation",
                "serializableModel",
                "interfaceOnly",
                "library",
                "useTags",
                "skipDefaultInterface",
                "hideGenerationTimestamp"})
        public final JkMultiValue<String> additionalProperties = JkMultiValue.of(String.class);



        private String toCmdLine() {
            return "generate " + String.join(" ", toArgs());
        }

        private List<String> toArgs() {
            List<String> args = new LinkedList<>();
            if (!JkUtilsString.isBlank(this.inputSpec)) {
                args.add("--input-spec");
                args.add(this.inputSpec);
            }
            if (!JkUtilsString.isBlank(this.generatorName)) {
                args.add("--generator-name");
                args.add(this.generatorName);
            }
            if (!JkUtilsString.isBlank(this.generationPackage)) {
                args.add("--api-package");
                args.add(this.generationPackage);
                args.add("--invoker-package");
                args.add(this.generationPackage);
                args.add("--model-package");
                args.add(this.generationPackage);
            }
            if (!generateTest) {
                args.add("--global-property");
                args.add("modelTests=false,apiTests=false");
            }
            if (this.outputDir != null && !JkUtilsString.isBlank(this.outputDir.toString())) {
                args.add("--output");
                args.add(this.outputDir.toString());
            }
            for(String optionName : options.getKeys()) {
                args.add("--" + optionName);
                args.add(options.get(optionName));
            }
            add(args, "--type-mappings", typeMappings);
            add(args, "--model-name-mappings", modelNameMappings);
            add(args, "--name-mappings", nameMappings);
            add(args, "--global-properties", globalProperties);
            add(args, "--additional-properties", additionalProperties);
            add(args, "--operation-id-name-mappings", operationIdNameMappings);
            add(args, "--parameter-name-mappings", parameterNameMappings);
            add(args, "--schema-mappings", schemaMappings);

            if (!additionalProperties.getKeys().contains("sourceFolder")) {
                args.add("--additional-properties");
                args.add("sourceFolder=/");
            }
            return args;
        }

        private static void add(List<String> args, String name, JkMultiValue<String> multiValue) {
            if (!multiValue.getKeys().isEmpty()) {
                args.add(name);
                args.add(flatten(multiValue));
            }
        }

        private static String flatten(JkMultiValue<String> multiValue) {
            List<String> items = new LinkedList<>();
            for (String key : multiValue.getKeys()) {
                String value = multiValue.get(key);
                if (JkUtilsString.isBlank(value)) {
                    items.add(key);
                } else {
                    items.add(key + "=" + value);
                }
            }
            return String.join(",", items);
        }

    }

}
