package org.pac4j.demo.dw.views;

import java.util.List;

import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.ProfileManager;

import io.dropwizard.views.common.View;

public class ProfilesView extends View {

    private final ProfileManager pm;

    public ProfilesView(ProfileManager pm) {
        super("/profiles.mustache");
        this.pm = pm;
    }

    public List<UserProfile> getProfiles() {
        return pm.getProfiles();
    }

}
