package util.SSO;


import org.eclipse.jetty.http.HttpCookie;
import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.matching.matcher.csrf.CsrfTokenGenerator;
import org.pac4j.core.matching.matcher.csrf.CsrfTokenGeneratorMatcher;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

public class CustomCsrfTokenGeneratorMatcher extends CsrfTokenGeneratorMatcher {

  private CsrfTokenGenerator csrfTokenGenerator;

  private String domain;

  private String path = "/";

  private Boolean httpOnly;

  private Boolean secure;

  private Integer maxAge;

  private HttpCookie.SameSite sameSite;

  public CustomCsrfTokenGeneratorMatcher(CsrfTokenGenerator csrfTokenGenerator) {
    super(csrfTokenGenerator);
    this.csrfTokenGenerator = csrfTokenGenerator;
    sameSite=HttpCookie.SameSite.NONE;
    secure=true;
  }

  @Override
  public boolean matches(final WebContext context) {
    CommonHelper.assertNotNull("csrfTokenGenerator", csrfTokenGenerator);
    final String token = csrfTokenGenerator.get(context);
    context.setRequestAttribute(Pac4jConstants.CSRF_TOKEN, token);
    final Cookie cookie = new Cookie(Pac4jConstants.CSRF_TOKEN, token);
    if (domain != null) {
      cookie.setDomain(domain);
    } else {
      cookie.setDomain(context.getServerName());
    }
    if (path != null) {
      cookie.setPath(path);
    }
    if (httpOnly != null) {
      cookie.setHttpOnly(httpOnly.booleanValue());
    }
    if (secure != null) {
      cookie.setSecure(secure.booleanValue());
    }
    if (maxAge != null) {
      cookie.setMaxAge(maxAge.intValue());
    }
    if(sameSite!=null) {
      if(sameSite.equals(HttpCookie.SameSite.NONE))
        cookie.setComment(HttpCookie.SAME_SITE_NONE_COMMENT);
      else if(sameSite.equals(HttpCookie.SameSite.LAX))
        cookie.setComment(HttpCookie.SAME_SITE_LAX_COMMENT);
      else if(sameSite.equals(HttpCookie.SameSite.STRICT))
        cookie.setComment(HttpCookie.SAME_SITE_STRICT_COMMENT);
    }
    return true;
  }
}
