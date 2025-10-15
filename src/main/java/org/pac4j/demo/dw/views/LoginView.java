package org.pac4j.demo.dw.views;

import org.pac4j.core.config.Config;
import org.pac4j.http.client.indirect.FormClient;

import io.dropwizard.views.common.View;

public class LoginView extends View {

    private final String callbackUrl;

    public LoginView(Config config) {
        super("/loginForm.mustache");

        final var formClient = (FormClient) config.getClients()
            .findClient("FormClient").orElseThrow();

        this.callbackUrl =
            formClient.getCallbackUrl() + "?client_name=FormClient";
    }

    public String getCallbackUrl() {
        return this.callbackUrl;
    }
}
