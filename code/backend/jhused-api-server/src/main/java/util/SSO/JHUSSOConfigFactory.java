package util.SSO;

import api.ApiServer;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.context.JEEContext;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;

public class JHUSSOConfigFactory implements ConfigFactory {

  private final String JHU_SSO_IDP = "https://idp.jh.edu/idp/shibboleth";

  public JHUSSOConfigFactory() {
  }


  @Override
  public Config build(Object... parameters) {

    //used the config from #119 in github
    final SAML2Configuration cfg =
        new SAML2Configuration(
            "resource:samlKeystore.jks",
            "k-d1bf-i4s7*sd5fj",
            "k-d1bf-i4s7*sd5fj",
            JHU_SSO_IDP);

    cfg.setMaximumAuthenticationLifetime(3600);
    cfg.setServiceProviderEntityId("https://jhused-api-server.herokuapp.com/callback?client_name=SAML2Client");
    cfg.setServiceProviderMetadataPath("sp-metadata-jhu.xml");
    cfg.setSpLogoutRequestSigned(true);
    final SAML2Client saml2Client = new SAML2Client(cfg);

    //only Saml2 client.
    final Clients clients = new Clients("https://jhused-api-server.herokuapp.com/callback", saml2Client);

    final Config config = new Config(clients);
    config.setHttpActionAdapter(new HttpActionAdapter());
    config.setSecurityLogic(CustomSecurityLogic.INSTANCE);
    return config;
  }
}
