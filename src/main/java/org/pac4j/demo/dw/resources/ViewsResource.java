package org.pac4j.demo.dw.resources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.pac4j.core.config.Config;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.demo.dw.views.IndexView;
import org.pac4j.demo.dw.views.LoginView;
import org.pac4j.demo.dw.views.ProfilesView;
import org.pac4j.jax.rs.annotations.Pac4JCallback;
import org.pac4j.jax.rs.annotations.Pac4JLogout;
import org.pac4j.jax.rs.annotations.Pac4JProfileManager;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

import io.dropwizard.views.View;

@Path("/")
public class ViewsResource {

    private final Config config;

    @Inject
    public ViewsResource(Config config) {
        this.config = config;
    }

    @GET
    public View index(@Pac4JProfileManager ProfileManager<CommonProfile> pm) {
        return new IndexView(pm);
    }

    @GET
    @Path("/facebook/{page}.html")
    @Pac4JSecurity(clients = "FacebookClient", authorizers = "securityHeaders", matchers = "excludedFbPath")
    public View fbProtected(
            @Pac4JProfileManager ProfileManager<CommonProfile> pm) {
        return new ProfilesView(pm);
    }

    @GET
    @Path("/saml2/index.html")
    @Pac4JSecurity(clients = "SAML2Client", authorizers = "securityHeaders")
    public View saml2(@Pac4JProfileManager ProfileManager<CommonProfile> pm) {
        return new ProfilesView(pm);
    }

    @GET
    @Path("/form/index.html")
    @Pac4JSecurity(clients = "FormClient", authorizers = "securityHeaders")
    public View form(@Pac4JProfileManager ProfileManager<CommonProfile> pm) {
        return new ProfilesView(pm);
    }

    @GET
    @Path("/basicauth/index.html")
    @Pac4JSecurity(clients = "IndirectBasicAuthClient", authorizers = "securityHeaders")
    public View basicauth(@Pac4JProfileManager ProfileManager<CommonProfile> pm) {
        return new ProfilesView(pm);
    }

    @GET
    @Path("/protected/index.html")
    @Pac4JSecurity(authorizers = "securityHeaders")
    public View protect(@Pac4JProfileManager ProfileManager<CommonProfile> pm) {
        return new ProfilesView(pm);
    }

    @GET
    @Path("/login.html")
    @Pac4JSecurity(clients = "AnonymousClient", authorizers = "mustBeAnon")
    public View login() {
        return new LoginView(config);
    }

    @POST
    @Path("/callback")
    @Pac4JCallback(multiProfile = true, renewSession = false, defaultUrl = "/views/")
    public void callbackPost() {
        // nothing to do here, pac4j handles everything
        // note that in jax-rs, you can't have two different http method on the
        // same resource method hence the duplication
    }

    @GET
    @Path("/callback")
    @Pac4JCallback(multiProfile = true, renewSession = false, defaultUrl = "/views/")
    public void callbackGet() {
        // nothing to do here, pac4j handles everything
        // note that in jax-rs, you can't have two different http method on the
        // same resource method hence the duplication
    }

    @GET
    @Path("/logout")
    @Pac4JSecurity(clients = "AnonymousClient", authorizers = "mustBeAuth")
    @Pac4JLogout(defaultUrl = "/views/?defaulturlafterlogout")
    public void logout() {
        // nothing to do here, pac4j handles everything
    }
}
