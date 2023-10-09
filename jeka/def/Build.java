import dev.jeka.core.api.crypto.gpg.JkGpg;
import dev.jeka.core.api.depmanagement.JkDependencySet;
import dev.jeka.core.api.depmanagement.JkRepo;
import dev.jeka.core.api.depmanagement.JkRepoSet;
import dev.jeka.core.api.depmanagement.artifact.JkStandardFileArtifactProducer;
import dev.jeka.core.api.depmanagement.publication.JkMavenPublication;
import dev.jeka.core.api.depmanagement.resolution.JkDependencyResolver;
import dev.jeka.core.api.file.JkPathSequence;
import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.java.JkJarPacker;
import dev.jeka.core.api.java.JkJavaCompileSpec;
import dev.jeka.core.api.java.JkJavaCompiler;
import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.api.system.JkLocator;
import dev.jeka.core.api.tooling.JkGitProcess;
import dev.jeka.core.tool.JkBean;
import dev.jeka.core.tool.JkInjectClasspath;
import dev.jeka.core.tool.JkInjectProperty;
import dev.jeka.core.tool.JkJekaVersionCompatibilityChecker;
import dev.jeka.core.tool.builtins.git.GitJkBean;
import dev.jeka.core.tool.builtins.project.ProjectJkBean;

import java.nio.file.Path;

@JkInjectClasspath("org.projectlombok:lombok:1.18.24")
class Build extends JkBean {

    ProjectJkBean projectKBean = getBean(ProjectJkBean.class);

    GitJkBean gitKBean = this.getBean(GitJkBean.class);

    @JkInjectProperty("OSSRH_USER")
    public String ossrhUser;

    @JkInjectProperty("OSSRH_PWD")
    public String ossrhPwd;

    @JkInjectProperty("JEKA_GPG_PASSPHRASE")
    public String secretRingPassword;

    Build() {
        projectKBean.lately(this::configure);
        gitKBean.configureProjectVersion = true;
    }

    private void configure(JkProject project) {
        JkPathTree sources = JkPathTree.of(this.getBaseDir().resolve("jeka/def"))
                .andMatching("dev/jeka/plugins/openapi/**");
        project.compilation.layout.setSources(sources.toSet());
        project.compilation.configureDependencies(deps -> deps
                .andFiles(JkLocator.getJekaJarPath())
                .and("org.projectlombok:lombok:1.18.24")
        );
        project.packaging.configureRuntimeDependencies(deps -> deps
                .minus(JkLocator.getJekaJarPath())
                .minus("org.projectlombok:lombok")
        );
        project.publication.maven
                .setModuleId("dev.jeka:openapi-plugin")
                .setPublishRepos(publishRepos())
                .pomMetadata
                    .addGithubDeveloper("Jerome Angibaud", "djeang_dev@yahoo.fr")
                    .setProjectUrl("https://jeka.dev")
                    .setScmUrl("https://github.com/jerkar/jeka.git")
                    .addApache2License();

        JkJekaVersionCompatibilityChecker.setCompatibilityRange(project.packaging.manifest,
                "0.10.26",
                "https://raw.githubusercontent.com/jeka-dev/openapi-plugin/master/breaking_versions.txt");
    }

    private JkRepoSet publishRepos() {
        JkRepo snapshotRepo = JkRepo.ofMavenOssrhDownloadAndDeploySnapshot(ossrhUser, ossrhPwd);
        JkGpg gpg = JkGpg.ofSecretRing(getBaseDir().resolve("jeka/secring.gpg"), secretRingPassword);

        JkRepo releaseRepo = JkRepo.ofMavenOssrhDeployRelease(ossrhUser, ossrhPwd,  gpg.getSigner(""));
        releaseRepo.publishConfig.setVersionFilter(version -> !version.isSnapshot());

        return  JkRepoSet.of(snapshotRepo, releaseRepo);
    }

}