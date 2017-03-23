package org.pac4j.demo.dw;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import org.pac4j.demo.dw.factory.Pac4jFactory1;
import org.pac4j.demo.dw.factory.Pac4jFactory2;

public class Pac4JDemoConfiguration extends Configuration {

    @JsonProperty("pac4j")
    Pac4jFactory1 pac4jFactory = new Pac4jFactory1();
    //Pac4jFactory2 pac4jFactory = new Pac4jFactory2();

}
