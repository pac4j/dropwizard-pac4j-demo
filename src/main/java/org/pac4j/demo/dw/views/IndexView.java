package org.pac4j.demo.dw.views;

import java.util.List;

import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.ProfileManager;

import io.dropwizard.views.common.View;

public class IndexView extends View {

    private final ProfileManager pm;

    public IndexView(ProfileManager pm) {
        super("/index.mustache");
        this.pm = pm;
    }

    public List<UserProfile> getProfiles() {
        return pm.getProfiles();
    }
}
