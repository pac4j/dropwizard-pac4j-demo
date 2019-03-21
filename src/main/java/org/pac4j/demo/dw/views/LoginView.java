package org.pac4j.demo.dw.views;

import org.pac4j.core.config.Config;
import org.pac4j.http.client.indirect.FormClient;

import io.dropwizard.views.View;

public class LoginView extends View {

    private String callbackUrl;

    public LoginView(Config config) {
        super("/loginForm.mustache");
        this.callbackUrl = config.getClients().findClient(FormClient.class)
                .getCallbackUrl() + "?client_name=FormClient";
    }

    public String getCallbackUrl() {
        return this.callbackUrl;
    }
}
