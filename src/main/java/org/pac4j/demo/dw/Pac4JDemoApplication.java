package org.pac4j.demo.dw;

import com.codahale.metrics.health.HealthCheck;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.pac4j.core.config.Config;
import org.pac4j.demo.dw.resources.ViewsResource;
import org.pac4j.dropwizard.Pac4jBundle;
import org.pac4j.dropwizard.Pac4jFactory;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.views.common.ViewBundle;
import org.pac4j.sql.test.tools.DbServer;

public class Pac4JDemoApplication extends Application<Pac4JDemoConfiguration> {

    public static void main(String[] args) throws Exception {
        new Pac4JDemoApplication().run(args);
    }

    private final Pac4jBundle<Pac4JDemoConfiguration> pac4j = new Pac4jBundle<>() {
        @Override
        public Pac4jFactory getPac4jFactory(
                Pac4JDemoConfiguration configuration) {
            return configuration.pac4jFactory;
        }
    };

    @Override
    public void initialize(Bootstrap<Pac4JDemoConfiguration> bootstrap) {
        bootstrap.addBundle(pac4j);
        bootstrap.addBundle(new ViewBundle<>());
    }

    @Override
    public void run(Pac4JDemoConfiguration conf, Environment env)
            throws Exception {

        env.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                // so that we can inject the config in resources if needed
                bind(pac4j.getConfig()).to(Config.class);
            }
        });

        env.jersey().register(ViewsResource.class);

        env.healthChecks().register("application", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        });

        new DbServer();
    }
}
