package util.SSO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.sparkjava.SparkHttpActionAdapter;
import org.pac4j.sparkjava.SparkWebContext;

import java.util.HashMap;

public class HttpActionAdapter extends SparkHttpActionAdapter {

  private final Gson gson;
    public HttpActionAdapter() {
      gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    @Override
    public Object adapt(final HttpAction action, final SparkWebContext context) {
        if (action != null) {
            final int code = action.getCode();
            if (code == HttpConstants.UNAUTHORIZED) {
                stop(401, gson.toJson("Forbidden, please sign in."));
            } else if (code == HttpConstants.FORBIDDEN) {
                stop(403, gson.toJson("Unauthorized."));
            } else {
                return super.adapt(action, context);
            }
        }
        throw new TechnicalException("No action provided");
    }

}
