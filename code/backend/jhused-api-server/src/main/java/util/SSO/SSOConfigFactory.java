package util.SSO;

import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.matching.matcher.PathMatcher;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
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

        final HeaderClient headerClient = new HeaderClient("Authorization", (credentials, ctx) -> {
            final String token = ((TokenCredentials) credentials).getToken();
            if (CommonHelper.isNotBlank(token)) {
                final CommonProfile profile = new CommonProfile();
                profile.setId(token);
                credentials.setUserProfile(profile);
            }
        });

        final Clients clients = new Clients("http://localhost:8080/callback", oidcClient, saml2Client, facebookClient,
                twitterClient, formClient, indirectBasicAuthClient, casClient, parameterClient, directBasicAuthClient, new AnonymousClient(),
                headerClient);

        final Config config = new Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
        config.addAuthorizer("custom", new CustomAuthorizer());
        config.addMatcher("excludedPath", new PathMatcher().excludeRegex("^/facebook/notprotected$"));
        config.setHttpActionAdapter(new DemoHttpActionAdapter(templateEngine));
        return config;
    }
}
