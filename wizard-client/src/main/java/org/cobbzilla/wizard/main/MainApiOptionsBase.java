package org.cobbzilla.wizard.main;

import lombok.Getter;
import lombok.Setter;
import org.cobbzilla.util.string.StringUtil;
import org.kohsuke.args4j.Option;

public abstract class MainApiOptionsBase extends MainOptionsBase {

    public static final String USAGE_ACCOUNT = "The account name. Required. The password must be in the appropriate environment variable";
    public static final String OPT_ACCOUNT = "-a";
    public static final String LONGOPT_ACCOUNT = "--account";
    @Option(name=OPT_ACCOUNT, aliases=LONGOPT_ACCOUNT, usage=USAGE_ACCOUNT, required=true)
    @Getter @Setter private String account;

    public static final String USAGE_API_BASE = "The server's API base URI. Default is http://127.0.0.1:3001/api";
    public static final String OPT_API_BASE = "-s";
    public static final String LONGOPT_API_BASE = "--server";
    @Option(name=OPT_API_BASE, aliases=LONGOPT_API_BASE, usage=USAGE_API_BASE)
    @Getter @Setter private String apiBase = getDefaultApiBaseUri();

    protected abstract String getDefaultApiBaseUri();

    @Getter private final String password = initPassword();

    protected boolean requireAccount() { return true; }

    private String initPassword() {
        if (!requireAccount()) return null;
        final String pass = System.getenv(getPasswordEnvVarName());
        if (StringUtil.empty(pass)) {
            System.err.println("No " + getPasswordEnvVarName() + " defined in environment");
            System.exit(2);
        }
        return pass;
    }

    protected abstract String getPasswordEnvVarName();

}
