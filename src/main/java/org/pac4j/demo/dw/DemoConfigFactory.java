package org.pac4j.demo.dw;

import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.authorization.authorizer.IsAnonymousAuthorizer;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.http.client.direct.DirectBasicAuthClient;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.http.client.indirect.IndirectBasicAuthClient;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;

public class DemoConfigFactory implements ConfigFactory {

    @Override
    public Config build(final Object... parameters) {
        final CasConfiguration configuration = new CasConfiguration("https://casserverpac4j.herokuapp.com/login");
        final CasClient casClient = new CasClient(configuration);
        final FormClient formClient = new FormClient("http://localhost:8080/login.html", new SimpleTestUsernamePasswordAuthenticator());
        final IndirectBasicAuthClient indirectBasicAuthClient = new IndirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        final DirectBasicAuthClient directBasicAuthClient = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        final Clients clients = new Clients("/callback", formClient, indirectBasicAuthClient, casClient, directBasicAuthClient, new AnonymousClient());

        final var config = new Config(clients);

        config.getClients().setAjaxRequestResolver(
            new DefaultAjaxRequestResolver());
        config.addAuthorizer("mustBeAuth",
            new IsAuthenticatedAuthorizer("/?mustBeAuth"));
        config.addAuthorizer("mustBeAnon",
            new IsAnonymousAuthorizer("/?mustBeAnon"));
        return config;
    }
}
