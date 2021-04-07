package util.SSO;

import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultSecurityClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.engine.AbstractExceptionAwareLogic;
import org.pac4j.core.engine.SecurityGrantedAccessAdapter;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.engine.decision.DefaultProfileStorageDecision;
import org.pac4j.core.engine.decision.ProfileStorageDecision;
import org.pac4j.core.engine.savedrequest.DefaultSavedRequestHandler;
import org.pac4j.core.engine.savedrequest.SavedRequestHandler;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.matching.checker.DefaultMatchingChecker;
import org.pac4j.core.matching.checker.MatchingChecker;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.Pac4jConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.*;

public class CustomSecurityLogic<R, C extends WebContext> extends AbstractExceptionAwareLogic<R, C> implements SecurityLogic<R, C> {

  public static final CustomSecurityLogic INSTANCE = new CustomSecurityLogic();

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomSecurityLogic.class);

  private ClientFinder clientFinder = new DefaultSecurityClientFinder();

  private AuthorizationChecker authorizationChecker = new DefaultAuthorizationChecker();

  private MatchingChecker matchingChecker = new CustomMatchingChecker();

  private ProfileStorageDecision profileStorageDecision = new DefaultProfileStorageDecision();

  private SavedRequestHandler savedRequestHandler = new DefaultSavedRequestHandler();

  @Override
  public R perform(final C context, final Config config,
                   final SecurityGrantedAccessAdapter<R, C> securityGrantedAccessAdapter,
                   final HttpActionAdapter<R, C> httpActionAdapter,
                   final String clients, final String authorizers, final String matchers,
                   final Boolean inputMultiProfile,
                   final Object... parameters) {

    LOGGER.debug("=== SECURITY ===");

    HttpAction action;
    try {


      // default value
      final boolean multiProfile = inputMultiProfile != null && inputMultiProfile;

      // checks
      assertNotNull("context", context);
      assertNotNull("config", config);
      assertNotNull("httpActionAdapter", httpActionAdapter);
      assertNotNull("clientFinder", clientFinder);
      assertNotNull("authorizationChecker", authorizationChecker);
      assertNotNull("matchingChecker", matchingChecker);
      assertNotNull("profileStorageDecision", profileStorageDecision);
      final Clients configClients = config.getClients();
      assertNotNull("configClients", configClients);

      // logic
      LOGGER.debug("url: {}", context.getFullRequestURL());
      LOGGER.debug("matchers: {}", matchers);
      if (matchingChecker.matches(context, matchers, config.getMatchers())) {

        LOGGER.debug("clients: {}", clients);
        final List<Client<? extends Credentials>> currentClients = clientFinder.find(configClients, context, clients);
        LOGGER.debug("currentClients: {}", currentClients);

        final boolean loadProfilesFromSession = profileStorageDecision.mustLoadProfilesFromSession(context,
            currentClients);
        LOGGER.debug("loadProfilesFromSession: {}", loadProfilesFromSession);
        context.setRequestAttribute(Pac4jConstants.LOAD_PROFILES_FROM_SESSION, loadProfilesFromSession);
        final ProfileManager<UserProfile> manager = getProfileManager(context);
        manager.setConfig(config);
        List<UserProfile> profiles = manager.getAll(loadProfilesFromSession);
        LOGGER.debug("profiles: {}", profiles);

        // no profile and some current clients
        if (isEmpty(profiles) && isNotEmpty(currentClients)) {
          boolean updated = false;
          // loop on all clients searching direct ones to perform authentication
          for (final Client currentClient : currentClients) {
            if (currentClient instanceof DirectClient) {
              LOGGER.debug("Performing authentication for direct client: {}", currentClient);

              final Optional<Credentials> credentials = currentClient.getCredentials(context);
              LOGGER.debug("credentials: {}", credentials);
              if (credentials.isPresent()) {
                final Optional<UserProfile> profile = currentClient.getUserProfile(credentials.get(), context);
                LOGGER.debug("profile: {}", profile);
                if (profile.isPresent()) {
                  final boolean saveProfileInSession = profileStorageDecision.mustSaveProfileInSession(context,
                      currentClients, (DirectClient) currentClient, profile.get());
                  LOGGER.debug("saveProfileInSession: {} / multiProfile: {}", saveProfileInSession, multiProfile);
                  manager.save(saveProfileInSession, profile.get(), multiProfile);
                  updated = true;
                  if (!multiProfile) {
                    break;
                  }
                }
              }
            }
          }
          if (updated) {
            profiles = manager.getAll(loadProfilesFromSession);
            LOGGER.debug("new profiles: {}", profiles);
          }
        }

        // we have profile(s) -> check authorizations
        if (isNotEmpty(profiles)) {
          LOGGER.debug("authorizers: {}", authorizers);
          if (authorizationChecker.isAuthorized(context, profiles, authorizers, config.getAuthorizers())) {
            LOGGER.debug("authenticated and authorized -> grant access");
            return securityGrantedAccessAdapter.adapt(context, profiles, parameters);
          } else {
            LOGGER.debug("forbidden");
            action = forbidden(context, currentClients, profiles, authorizers);
          }
        } else {
          if (startAuthentication(context, currentClients)) {
            LOGGER.debug("Starting authentication");
            saveRequestedUrl(context, currentClients, config.getClients().getAjaxRequestResolver());
            action = redirectToIdentityProvider(context, currentClients);
          } else {
            LOGGER.debug("unauthorized");
            action = unauthorized(context, currentClients);
          }
        }

      } else {

        LOGGER.debug("no matching for this request -> grant access");
        return securityGrantedAccessAdapter.adapt(context, Arrays.asList(), parameters);
      }

    } catch (final Exception e) {
      return handleException(e, httpActionAdapter, context);
    }

    return httpActionAdapter.adapt(action, context);
  }

  /**
   * Return a forbidden error.
   *
   * @param context        the web context
   * @param currentClients the current clients
   * @param profiles       the current profiles
   * @param authorizers    the authorizers
   * @return a forbidden error
   */
  protected HttpAction forbidden(final C context, final List<Client<? extends Credentials>> currentClients,
                                 final List<UserProfile> profiles, final String authorizers) {
    return ForbiddenAction.INSTANCE;
  }

  /**
   * Return whether we must start a login process if the first client is an indirect one.
   *
   * @param context        the web context
   * @param currentClients the current clients
   * @return whether we must start a login process
   */
  protected boolean startAuthentication(final C context, final List<Client<? extends Credentials>> currentClients) {
    return isNotEmpty(currentClients) && currentClients.get(0) instanceof IndirectClient;
  }

  /**
   * Save the requested url.
   *
   * @param context        the web context
   * @param currentClients the current clients
   */
  protected void saveRequestedUrl(final C context, final List<Client<? extends Credentials>> currentClients,
                                  final AjaxRequestResolver ajaxRequestResolver) {
    if (ajaxRequestResolver == null || !ajaxRequestResolver.isAjax(context)) {
      savedRequestHandler.save(context);
    }
  }

  /**
   * Perform a redirection to start the login process of the first indirect client.
   *
   * @param context        the web context
   * @param currentClients the current clients
   * @return the performed redirection
   */
  protected HttpAction redirectToIdentityProvider(final C context,
                                                  final List<Client<? extends Credentials>> currentClients) {
    final IndirectClient currentClient = (IndirectClient) currentClients.get(0);
    return (HttpAction) currentClient.getRedirectionAction(context).get();
  }

  /**
   * Return an unauthorized error.
   *
   * @param context        the web context
   * @param currentClients the current clients
   * @return an unauthorized error
   */
  protected HttpAction unauthorized(final C context, final List<Client<? extends Credentials>> currentClients) {
    return UnauthorizedAction.INSTANCE;
  }

  public ClientFinder getClientFinder() {
    return clientFinder;
  }

  public void setClientFinder(final ClientFinder clientFinder) {
    this.clientFinder = clientFinder;
  }

  public AuthorizationChecker getAuthorizationChecker() {
    return authorizationChecker;
  }

  public void setAuthorizationChecker(final AuthorizationChecker authorizationChecker) {
    this.authorizationChecker = authorizationChecker;
  }

  public MatchingChecker getMatchingChecker() {
    return matchingChecker;
  }

  public void setMatchingChecker(final MatchingChecker matchingChecker) {
    this.matchingChecker = matchingChecker;
  }

  public ProfileStorageDecision getProfileStorageDecision() {
    return profileStorageDecision;
  }

  public void setProfileStorageDecision(final ProfileStorageDecision profileStorageDecision) {
    this.profileStorageDecision = profileStorageDecision;
  }

  public SavedRequestHandler getSavedRequestHandler() {
    return savedRequestHandler;
  }

  public void setSavedRequestHandler(final SavedRequestHandler savedRequestHandler) {
    this.savedRequestHandler = savedRequestHandler;
  }

  @Override
  public String toString() {
    return toNiceString(this.getClass(), "clientFinder", this.clientFinder, "authorizationChecker",
        this.authorizationChecker,
        "matchingChecker", this.matchingChecker, "profileStorageDecision", this.profileStorageDecision,
        "errorUrl", getErrorUrl(), "savedRequestHandler", savedRequestHandler);
  }
}
