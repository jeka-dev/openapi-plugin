import dev.jeka.core.api.crypto.gpg.JkGpg;
import dev.jeka.core.api.depmanagement.JkDependencySet;
import dev.jeka.core.api.depmanagement.JkRepo;
import dev.jeka.core.api.depmanagement.JkRepoProperties;
import dev.jeka.core.api.depmanagement.JkRepoSet;
import dev.jeka.core.api.depmanagement.artifact.JkArtifactLocator;
import dev.jeka.core.api.depmanagement.artifact.JkStandardFileArtifactProducer;
import dev.jeka.core.api.depmanagement.artifact.JkSuppliedFileArtifactProducer;
import dev.jeka.core.api.depmanagement.publication.JkMavenPublication;
import dev.jeka.core.api.depmanagement.publication.JkPomMetadata;
import dev.jeka.core.api.depmanagement.resolution.JkDependencyResolver;
import dev.jeka.core.api.file.JkPathSequence;
import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.java.JkJarPacker;
import dev.jeka.core.api.java.JkJavaCompileSpec;
import dev.jeka.core.api.java.JkJavaCompiler;
import dev.jeka.core.api.system.JkLocator;
import dev.jeka.core.api.tooling.JkGitProcess;
import dev.jeka.core.tool.JkBean;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.JkInjectClasspath;
import sun.tools.jar.resources.jar;

import java.nio.file.Path;

@JkInjectClasspath("org.projectlombok:lombok:1.18.24")
class Build extends JkBean {

    private JkPathTree sources = JkPathTree.of("jeka/def").andMatching("dev/jeka/plugins/openapi/**");

    private Path compileDir = this.getOutputDir().resolve("plugin-classes");

    private String ossrhUser;
    private String ossrhPwd;

    private void makeJar(Path jar) {
        cleanOutput();
        JkDependencySet deps = JkDependencySet.of("org.projectlombok:lombok:1.18.24")
                .andFiles(JkLocator.getJekaJarPath());
        JkPathSequence depFiles = JkDependencyResolver.of().resolve(deps).getFiles();
        JkJavaCompileSpec compileSpec = JkJavaCompileSpec.of()
                .setSources(sources.toSet())
                .setClasspath(depFiles)
                .setOutputDir(compileDir);
        JkJavaCompiler.of().compile(compileSpec);
        JkJarPacker.of(compileDir).makeJar(jar);
    }

    public void publish() {
        JkStandardFileArtifactProducer artifactLocator = JkStandardFileArtifactProducer.of();
        artifactLocator.putMainArtifact(this::makeJar);
        JkMavenPublication mavenPublication = JkMavenPublication.of();
        mavenPublication.setArtifactLocator(artifactLocator);

        mavenPublication.setModuleId("dev.jeka:openapi-plugin");
        mavenPublication.setVersion(JkGitProcess.of().extractSuffixFromLastCommitMessage("Release:"));

        mavenPublication.pomMetadata
                .addGithubDeveloper("Jerome Angibaud", "djeang_dev@yahoo.fr")
                .setProjectUrl("https://jeka.dev")
                .setScmUrl("https://github.com/jerkar/jeka.git")
                .addApache2License();

        mavenPublication
                .setPublishRepos(publishRepos())
                .publish();
    }

    private JkRepoSet publishRepos() {
        JkRepo snapshotRepo = JkRepo.ofMavenOssrhDownloadAndDeploySnapshot(ossrhUser, ossrhPwd);
        JkGpg gpg = JkGpg.ofStandardProject(this.getBaseDir());

        JkRepo releaseRepo =  JkRepo.ofMavenOssrhDeployRelease(ossrhUser, ossrhPwd,  gpg.getSigner(""));
        releaseRepo.publishConfig.setVersionFilter(jkVersion -> !jkVersion.isSnapshot());

        JkRepo githubRepo = JkRepo.ofGitHub("jeka-dev", "jeka");
        githubRepo.publishConfig.setVersionFilter(jkVersion -> !jkVersion.isSnapshot());
        return  JkRepoSet.of(snapshotRepo, releaseRepo, githubRepo);
    }

}