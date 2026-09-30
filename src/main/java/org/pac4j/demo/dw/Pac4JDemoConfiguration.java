package org.pac4j.demo.dw;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.pac4j.dropwizard.Pac4jFactory;

public class Pac4JDemoConfiguration extends Configuration {

    @NotNull
    @Valid
    @JsonProperty("pac4j")
    Pac4jFactory pac4jFactory = new Pac4jFactory();

}
