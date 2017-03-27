package org.pac4j.demo.dw.factory;

import org.pac4j.core.authorization.authorizer.IsAnonymousAuthorizer;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.jax.rs.pac4j.JaxRsConfig;
import org.pac4j.oauth.client.FacebookClient;

import java.util.Map;

/**
 * A demo configuration factory.
 */
public class DemoConfigFactory1 implements ConfigFactory {

    @Override
    public Config build(final Object... parameters) {
        final Map<String, String> properties = (Map<String, String>) parameters[0];

        final AnonymousClient anonymousClient = new AnonymousClient();

        final FormClient formClient = new FormClient(properties.get("loginUrl"), new SimpleTestUsernamePasswordAuthenticator());

        final FacebookClient facebookClient = new FacebookClient(properties.get("fbKey"), properties.get("fbSecret"));

        final Clients clients = new Clients(properties.get("callbackUrl"), anonymousClient, formClient, facebookClient);

        final JaxRsConfig config = new JaxRsConfig();
        config.setClients(clients);

        config.addAuthorizer("mustBeAnon", new IsAnonymousAuthorizer("/views/?mustBeAuth"));
        config.addAuthorizer("mustBeAuth", new IsAuthenticatedAuthorizer("/views/?mustBeAnon"));
        config.addMatcher("excludedFbPath", new PathMatcher().excludeRegex("^/views/facebook/notprotected\\.html$"));

        return config;
    }
}
