package org.pac4j.demo.dw.factory;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.pac4j.config.client.PropertiesConfigFactory;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.dropwizard.Pac4jFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * This Pac4j factory relies on client properties {@link #clientsProperties} to define all the clients.
 */
public class Pac4jFactory2 extends Pac4jFactory {

    private Map<String, String> clientsProperties = new HashMap<>();

    public Map<String, String> getClientsProperties() {
        return clientsProperties;
    }

    @JsonProperty
    public void setClientsProperties(Map<String, String> clientsProperties) {
	    if (clientsProperties != null) {
          this.clientsProperties.putAll(clientsProperties);
		}        
    }

    @Override
    public Config build() {
        Clients clientsComponent = new Clients();
        Config config = new Config(clientsComponent);

        if (getCallbackUrl() != null) {
            clientsComponent.setCallbackUrl(getCallbackUrl());
        }
        if (getClientNameParameter() != null) {
            clientsComponent.setClientNameParameter(getClientNameParameter());
        }
        clientsComponent.setCallbackUrlResolver(getCallbackUrlResolver());
        clientsComponent.setAuthorizationGenerators(getAuthorizationGenerators());

        clientsComponent.setClients(getClients());

        final PropertiesConfigFactory propertiesConfigFactory = new PropertiesConfigFactory(clientsProperties);
        final Config newConfig = propertiesConfigFactory.build();
        clientsComponent.getClients().addAll(newConfig.getClients().getClients());

        /*

        This is certainly a check we should do in pac4j core.

        if (getDefaultClient() != null) {
            boolean found = false;
            for (Client c : clients) {
                if (getDefaultClient().equals(c.getName())) {
                    clientsComponent.setDefaultClient(c);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("Client '" + getDefaultClient()
                    + "' is not one of the configured clients");
            }
        }*/

        config.setSecurityLogic(getSecurityLogic());
        config.setCallbackLogic(getCallbackLogic());
        config.setLogoutLogic(getApplicationLogoutLogic());
        config.setHttpActionAdapter(getHttpActionAdapter());
        config.setAuthorizers(getAuthorizers());
        config.setMatchers(getMatchers());

        return config;
    }
}
