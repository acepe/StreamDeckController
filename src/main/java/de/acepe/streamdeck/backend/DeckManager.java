package de.acepe.streamdeck.backend;

import de.acepe.streamdeck.backend.config.*;
import de.acepe.streamdeck.device.IStreamDeck;
import de.acepe.streamdeck.device.StreamDeckDevices;
import de.acepe.streamdeck.device.event.KeyEvent;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import static de.acepe.streamdeck.device.event.KeyEvent.Type.PRESSED;
import static de.acepe.streamdeck.device.event.KeyEvent.Type.RELEASED;

public class DeckManager {
    private static final Logger LOG = LoggerFactory.getLogger(DeckManager.class);

    private final List<Profile> profiles = new ArrayList<>(1);
    private final HashMap<String, Page> pages = new HashMap<>();

    private final StreamDeckDevices streamDeckDevices;
    private final Persistence persistence;
    private final Settings settings;

    private Consumer<Integer> uiCallback;
    private Profile currentProfile;
    private Page currentPage;

    @Inject
    DeckManager(StreamDeckDevices streamDeckDevices, Persistence persistence) {
        this.streamDeckDevices = streamDeckDevices;
        this.persistence = persistence;

//        Profile defaultProfile = persistence.createDefaultProfile();
//        addProfile(defaultProfile);
        settings = persistence.loadSettings();
        persistence.loadAllProfiles().forEach(this::addProfile);

        setCurrentProfile(settings.getCurrentProfile());

        streamDeckDevices.registerDecksDiscoveredCallback(this::onStreamdeckConnected);
        onStreamdeckConnected(streamDeckDevices.getStreamDeck());
    }

    public Profile loadProfile(String profileName) {
        Profile profile = persistence.loadProfile(profileName);
        addProfile(profile);
        setCurrentPage(profile.getStartPage());
        return profile;
    }

    public void addProfile(Profile profile) {
        profiles.add(profile);
        profile.getPages().forEach(this::registerPage);
    }

    public void saveProfiles() {
        profiles.forEach(persistence::saveProfile);
    }

    private void onStreamdeckConnected(IStreamDeck deck) {
        deck.addKeyListener(this::onDeckEventsReceived);
        setCurrentPage(getCurrentPage().getId());
    }

    public void bindUpdateUICallback(Consumer<Integer> uiCallback) {
        this.uiCallback = uiCallback;
    }

    public void updateButtonUi(int buttonIndex) {
        if (uiCallback == null) {
            return;
        }
        uiCallback.accept(buttonIndex);
    }

    public void registerPage(Page page) {
        pages.put(page.getId(), page);
    }

    public void setCurrentProfile(String profileName) {
        Profile profile = profiles.stream()
                                  .filter(p -> p.getName().equals(profileName)).findFirst()
                                  .orElse(profiles.get(0));
        setCurrentProfile(profile);
    }

    public void setCurrentProfile(Profile profile) {
        currentProfile = profile;
        setCurrentPage(profile.getStartPage());

        settings.setCurrentProfile(profile.getName());
        persistence.saveSettings(settings);
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public void setCurrentPage(String pageId) {
        Page nextPage = pages.get(pageId);
        if (nextPage == null) {
            LOG.error("Page {} not found. It is not contained in any profile", pageId);
            return;
        }
        if (currentPage != null) {
            currentPage.unbindDeckManager();
        }

        this.currentPage = nextPage;
        this.currentPage.bindDeckManager(this);
        this.currentPage.update();
    }

    private void onDeckEventsReceived(KeyEvent keyEvent) {
        if (currentPage == null) {
            return;
        }

        DeckButton deckButton = currentPage.getButton(keyEvent.getKeyId());

        if (deckButton != null) {
            KeyEvent.Type type = keyEvent.getType();
            if (type == PRESSED) {
                deckButton.onButtonPressed(keyEvent);
            } else if (type == RELEASED) {
                deckButton.onButtonReleased(keyEvent);
            }
        }
    }

    public void fireActionFromUI(int index) {
        currentPage.fireActionFromUI(index, getDeck());
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    public IStreamDeck getDeck() {
        return streamDeckDevices.getStreamDeck();
    }

    public Image getButtonImage(int buttonIndex) {
        return getCurrentPage().getButton(buttonIndex).getImage();
    }

    public List<Profile> getProfiles() {
        return profiles;
    }
}
