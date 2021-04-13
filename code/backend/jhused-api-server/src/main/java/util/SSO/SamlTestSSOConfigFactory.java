package util.SSO;

import api.ApiServer;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;

public class SamlTestSSOConfigFactory implements ConfigFactory {

  public SamlTestSSOConfigFactory() {
  }


  @Override
  public Config build(Object... parameters) {

    //used the config from #119 in github
    final SAML2Configuration cfg =
        new SAML2Configuration(
            "resource:samlKeystore.jks",
            "k-d1bf-i4s7*sd5fj",
            "k-d1bf-i4s7*sd5fj",
            "resource:metadata-samltest.xml");

    cfg.setMaximumAuthenticationLifetime(3600);
    cfg.setServiceProviderEntityId("https://localhost:8080/callback?client_name=SAML2Client");
    cfg.setServiceProviderMetadataPath("sp-metadata-local.xml");
    cfg.setPostLogoutURL(ApiServer.BACKEND_URL+"/redirectToFrontend");
    cfg.setSpLogoutRequestBindingType(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
    cfg.setSpLogoutRequestSigned(true);
    final SAML2Client saml2Client = new SAML2Client(cfg);

    //only Saml2 client.
    final Clients clients = new Clients("https://localhost:8080/callback", saml2Client);

    final Config config = new Config(clients);
    config.setHttpActionAdapter(new HttpActionAdapter());
    config.setSecurityLogic(CustomSecurityLogic.INSTANCE);
    return config;
  }
}
