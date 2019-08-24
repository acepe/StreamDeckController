package de.acepe.streamdeck.backend.config;

import com.google.gson.annotations.Expose;

public class Settings {

    @Expose
    private String currentProfile;

    public Settings() {
    }

    public String getCurrentProfile() {
        return currentProfile;
    }

    public void setCurrentProfile(String currentProfile) {
        this.currentProfile = currentProfile;
    }
}
