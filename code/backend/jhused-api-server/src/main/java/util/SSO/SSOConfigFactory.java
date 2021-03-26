package util.SSO;

import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import spark.TemplateEngine;

public class SSOConfigFactory implements ConfigFactory {

    private final String salt;
    private final TemplateEngine templateEngine;

    public SSOConfigFactory(final String salt, final TemplateEngine templateEngine) {
        this.salt = salt;
        this.templateEngine = templateEngine;
    }


    @Override
    public Config build(Object... parameters) {

        final SAML2Configuration cfg = new SAML2Configuration("resource:samlKeystore.jks", "pac4j-demo-passwd",
                "pac4j-demo-passwd", "resource:metadata-okta.xml");
        cfg.setMaximumAuthenticationLifetime(3600);
        cfg.setServiceProviderEntityId("http://localhost:8080/callback?client_name=SAML2Client");
        cfg.setServiceProviderMetadataPath("sp-metadata.xml");
        final SAML2Client saml2Client = new SAML2Client(cfg);

        return null;
    }
}
