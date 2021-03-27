package util.SSO;

import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;

public class SSOConfigFactory implements ConfigFactory {

  public SSOConfigFactory() {
  }


  @Override
  public Config build(Object... parameters) {

    //used the config from #119 in github
    final SAML2Configuration cfg =
        new SAML2Configuration(
            "resource:samlKeystore.jks",
            "k-d1bf-i4s7*sd5fj",
            "k-d1bf-i4s7*sd5fj",
            "https://idp.jh.edu/idp/shibboleth");

    cfg.setMaximumAuthenticationLifetime(3600);
    cfg.setServiceProviderEntityId("https://jhused-api-server.herokuapp.com/callback?client_name=SAML2Client");
    cfg.setServiceProviderMetadataPath("sp-metadata.xml");
    final SAML2Client saml2Client = new SAML2Client(cfg);

    //only Saml2 client.
    final Clients clients = new Clients("http://localhost:4567/callback", saml2Client);

    //TODO figure out this authorizer stuff
    final Config config = new Config(clients);
//        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
    config.addAuthorizer("custom", new CustomAuthorizer());
    config.setHttpActionAdapter(new HttpActionAdapter());
    return config;
  }
}
