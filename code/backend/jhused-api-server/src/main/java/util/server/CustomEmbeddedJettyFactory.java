package util.server;

import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import spark.ExceptionMapper;
import spark.embeddedserver.EmbeddedServer;
import spark.embeddedserver.EmbeddedServerFactory;
import spark.embeddedserver.jetty.*;
import spark.http.matching.MatcherFilter;
import spark.route.Routes;
import spark.staticfiles.StaticFilesConfiguration;


/**
 * This is the main entry point for customization operations
 **/
public class CustomEmbeddedJettyFactory implements EmbeddedServerFactory {
  private final JettyServerFactory serverFactory;
  private ThreadPool threadPool;
  private boolean httpOnly = true;

  public CustomEmbeddedJettyFactory() {
    this.serverFactory = new CustomJettyServer();
  }

  public CustomEmbeddedJettyFactory(JettyServerFactory serverFactory) {
    this.serverFactory = serverFactory;
  }

  public EmbeddedServer create(Routes routeMatcher,
                               StaticFilesConfiguration staticFilesConfiguration,
                               ExceptionMapper exceptionMapper,
                               boolean hasMultipleHandler) {
    MatcherFilter matcherFilter = new MatcherFilter(routeMatcher, staticFilesConfiguration, exceptionMapper, false,
        hasMultipleHandler);
    matcherFilter.init(null);

    JettyHandler handler = new JettyHandler(matcherFilter);
    handler.getSessionCookieConfig().setHttpOnly(httpOnly);
    handler.getSessionCookieConfig().setSecure(true);
    handler.setSameSite(HttpCookie.SameSite.NONE);
    return new EmbeddedJettyServer(serverFactory, handler).withThreadPool(threadPool);
  }

  /**
   * Sets optional thread pool for jetty server.  This is useful for overriding the default thread pool
   * behaviour for example io.dropwizard.metrics.jetty9.InstrumentedQueuedThreadPool.
   *
   * @param threadPool thread pool
   * @return Builder pattern - returns this instance
   */
  public CustomEmbeddedJettyFactory withThreadPool(ThreadPool threadPool) {
    this.threadPool = threadPool;
    return this;
  }

  public CustomEmbeddedJettyFactory withHttpOnly(boolean httpOnly) {
    this.httpOnly = httpOnly;
    return this;
  }

}
