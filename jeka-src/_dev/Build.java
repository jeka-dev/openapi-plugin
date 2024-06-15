package _dev;

import dev.jeka.core.api.crypto.gpg.JkGpgSigner;
import dev.jeka.core.api.depmanagement.JkRepoSet;
import dev.jeka.core.tool.JkInjectClasspath;
import dev.jeka.core.tool.JkInjectProperty;
import dev.jeka.core.tool.JkJekaVersionRanges;
import dev.jeka.core.tool.KBean;
import dev.jeka.core.tool.builtins.base.BaseKBean;
import dev.jeka.core.tool.builtins.tooling.git.GitKBean;
import dev.jeka.core.tool.builtins.tooling.maven.MavenKBean;
import dev.jeka.plugins.nexus.NexusKBean;

@JkInjectClasspath("org.projectlombok:lombok:1.18.24")
@JkInjectClasspath("dev.jeka:nexus-plugin")
class Build extends KBean {

    @JkInjectProperty("OSSRH_USER")
    public String ossrhUser;

    @JkInjectProperty("OSSRH_PWD")
    public String ossrhPwd;

    @JkInjectProperty("JEKA_GPG_PASSPHRASE")
    public String secretRingPassword;

    private final BaseKBean baseKBean = load(BaseKBean.class);

    protected void init() {

        JkJekaVersionRanges.setCompatibilityRange(baseKBean.getManifest(),
                "0.11.0-alpha.3",
                "https://raw.githubusercontent.com/jeka-dev/openapi-plugin/master/breaking_versions.txt");

        // Publish on ossrh
        load(MavenKBean.class).getMavenPublication()
                .setModuleId("dev.jeka:openapi-plugin")
                .setRepos(publishRepos())
                .pomMetadata
                    .setProjectName("OpenApi plugin for JeKa")
                    .setProjectDescription("OpenApi plugin for JeKa")
                    .addGithubDeveloper("Jerome Angibaud", "djeang_dev@yahoo.fr")
                    .setProjectUrl("https://github.com/jeka-dev/openapi-plugin")
                    .setScmUrl("https://github.com/jeka-dev/openapi-plugin.git")
                    .addApache2License();
        load(NexusKBean.class); // Loading this KBean will automatically configure MavenKBean for publishing on Nexus

        load(GitKBean.class).handleVersioning = true;

    }

    private JkRepoSet publishRepos() {
        JkGpgSigner gpgSigner = JkGpgSigner.ofStandardProject(getBaseDir());
        return JkRepoSet.ofOssrhSnapshotAndRelease(ossrhUser, ossrhPwd, gpgSigner);
    }

}