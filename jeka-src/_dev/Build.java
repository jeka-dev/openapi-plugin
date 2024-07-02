package _dev;

import dev.jeka.core.api.tooling.git.JkGit;
import dev.jeka.core.tool.JkInjectClasspath;
import dev.jeka.core.tool.JkJekaVersionRanges;
import dev.jeka.core.tool.KBean;
import dev.jeka.core.tool.builtins.base.BaseKBean;

@JkInjectClasspath("org.projectlombok:lombok:1.18.24")
class Build extends KBean {

    private final BaseKBean baseKBean = load(BaseKBean.class);

    protected void init() {
        JkJekaVersionRanges.setCompatibilityRange(baseKBean.getManifest(),
                "0.11.0-alpha.3",
                "https://raw.githubusercontent.com/jeka-dev/openapi-plugin/master/breaking_versions.txt");
        baseKBean.setVersionSupplier(JkGit.of()::getJkVersionFromTag);
    }

}