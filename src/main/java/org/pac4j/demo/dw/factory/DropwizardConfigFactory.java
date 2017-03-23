package org.pac4j.demo.dw.factory;

import org.pac4j.core.config.ConfigFactory;

import java.util.Map;

public interface DropwizardConfigFactory extends ConfigFactory {

    void setProperties(Map<String, String> properties);
}
