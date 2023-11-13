import dev.jeka.core.api.crypto.gpg.JkGpg;
import dev.jeka.core.api.depmanagement.JkRepo;
import dev.jeka.core.api.depmanagement.JkRepoSet;
import dev.jeka.core.api.depmanagement.publication.JkNexusRepos;
import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.file.JkPathTreeSet;
import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.api.system.JkLocator;
import dev.jeka.core.tool.JkBean;
import dev.jeka.core.tool.JkInjectClasspath;
import dev.jeka.core.tool.JkInjectProperty;
import dev.jeka.core.tool.JkJekaVersionCompatibilityChecker;
import dev.jeka.core.tool.builtins.git.JkVersionFromGit;
import dev.jeka.core.tool.builtins.project.ProjectJkBean;
import dev.jeka.core.tool.builtins.repos.NexusJkBean;

@JkInjectClasspath("org.projectlombok:lombok:1.18.24")
class Build extends JkBean {

    @JkInjectProperty("OSSRH_USER")
    public String ossrhUser;

    @JkInjectProperty("OSSRH_PWD")
    public String ossrhPwd;

    @JkInjectProperty("JEKA_GPG_PASSPHRASE")
    public String secretRingPassword;

    Build() {
        getBean(ProjectJkBean.class).lately(this::configure);
        getBean(NexusJkBean.class); // load Nexus KBean to handle Nexus repo
    }

    private void configure(JkProject project) {
        JkPathTreeSet sources = JkPathTree.of(this.getBaseDir().resolve("jeka/def"))
                .andMatching("dev/jeka/plugins/openapi/**")
                .toSet();
        project.compilation.layout.setSources(sources);
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
                    .setProjectDescription("OpenApi plugin for JeKa")
                    .addGithubDeveloper("Jerome Angibaud", "djeang_dev@yahoo.fr")
                    .setProjectUrl("https://github.com/jeka-dev/openapi-plugin")
                    .setScmUrl("https://github.com/jeka-dev/openapi-plugin.git")
                    .addApache2License();
        JkVersionFromGit.of().handleVersioning(project);
        JkNexusRepos.handleAutoRelease(project);

        JkJekaVersionCompatibilityChecker.setCompatibilityRange(project.packaging.manifest,
                "0.10.37",
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