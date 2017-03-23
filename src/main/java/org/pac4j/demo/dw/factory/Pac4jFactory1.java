package org.pac4j.demo.dw.factory;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.pac4j.core.config.Config;
import org.pac4j.core.exception.TechnicalException;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.pac4j.dropwizard.Pac4jFactory;

/**
 * This Pac4j factory relies on:
 * - properties ({@link #configProperties}) defining the whole pac4j configuration
 * - a configuration class (@link #configClass) which is built using the previous properties.
 */
public class Pac4jFactory1 extends Pac4jFactory {

    @NotNull
    private Map<String, String> configProperties = new HashMap<>();

    @NotNull
    private String configClass;

    public Map<String, String> getConfigProperties() {
        return configProperties;
    }

    @JsonProperty
    public void setConfigProperties(Map<String, String> configProperties) {
        this.configProperties = configProperties;
    }

    public String getConfigClass() {
        return configClass;
    }

    @JsonProperty
    public void setConfigClass(String configClass) {
        this.configClass = configClass;
    }

    @Override
    public Config build() {

        // we can't use the ConfigBuilder here as we need to pass parameters
        try {
            final Class<DropwizardConfigFactory> configFactoryClass = (Class<DropwizardConfigFactory>) Class.forName(configClass);
            final DropwizardConfigFactory factory = configFactoryClass.newInstance();
            factory.setProperties(configProperties);
            return factory.build();
        } catch (final Exception e) {
            throw new TechnicalException(e);
        }
    }
}
