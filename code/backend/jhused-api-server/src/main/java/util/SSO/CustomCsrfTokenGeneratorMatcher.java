package util.SSO;


import org.eclipse.jetty.http.HttpCookie;
import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.matching.matcher.csrf.CsrfTokenGenerator;
import org.pac4j.core.matching.matcher.csrf.CsrfTokenGeneratorMatcher;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

public class CustomCsrfTokenGeneratorMatcher implements Matcher{

  private CsrfTokenGenerator csrfTokenGenerator;

  private String domain;

  private String path = "/";

  private Boolean httpOnly;

  private Boolean secure;

  private Integer maxAge;

  private HttpCookie.SameSite sameSite;

  public CustomCsrfTokenGeneratorMatcher(final CsrfTokenGenerator csrfTokenGenerator) {
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
    context.addResponseCookie(cookie);
    return true;
  }

  public CsrfTokenGenerator getCsrfTokenGenerator() {
    return csrfTokenGenerator;
  }

  public void setCsrfTokenGenerator(final CsrfTokenGenerator csrfTokenGenerator) {
    this.csrfTokenGenerator = csrfTokenGenerator;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(final String domain) {
    this.domain = domain;
  }

  public String getPath() {
    return path;
  }

  public void setPath(final String path) {
    this.path = path;
  }

  public Boolean getHttpOnly() {
    return httpOnly;
  }

  public void setHttpOnly(final Boolean httpOnly) {
    this.httpOnly = httpOnly;
  }

  public Boolean getSecure() {
    return secure;
  }

  public void setSecure(final Boolean secure) {
    this.secure = secure;
  }

  public Integer getMaxAge() {
    return maxAge;
  }

  public void setMaxAge(Integer maxAge) {
    this.maxAge = maxAge;
  }

  public void setSameSite(HttpCookie.SameSite sameSite) {
    this.sameSite = sameSite;
  }

  @Override
  public String toString() {
    return CommonHelper.toNiceString(this.getClass(), "csrfTokenGenerator", csrfTokenGenerator, "domain", domain, "path", path,
        "httpOnly", httpOnly, "secure", secure, "maxAge", maxAge);
  }
}
