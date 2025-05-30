package _dev;

import dev.jeka.core.api.tooling.git.JkGit;
import dev.jeka.core.tool.JkDep;
import dev.jeka.core.tool.JkJekaVersionRanges;
import dev.jeka.core.tool.JkPostInit;
import dev.jeka.core.tool.KBean;
import dev.jeka.core.tool.builtins.base.BaseKBean;

@JkDep("org.projectlombok:lombok:1.18.24")
class Custom extends KBean {

    @JkPostInit
    private void postInit(BaseKBean baseKBean) {
        JkJekaVersionRanges.setCompatibilityRange(baseKBean.getManifest(),
                "0.11.38",
                "https://raw.githubusercontent.com/jeka-dev/openapi-plugin/master/breaking_versions.txt");
        baseKBean.setVersionSupplier(JkGit.of()::getJkVersionFromTag);
    }

}