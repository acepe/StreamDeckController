package de.acepe.streamdeck.backend;

import de.acepe.streamdeck.backend.config.Page;
import de.acepe.streamdeck.device.IStreamDeck;
import de.acepe.streamdeck.device.StreamDeckDevices;
import de.acepe.streamdeck.device.event.KeyEvent;
import javafx.scene.image.Image;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

import static de.acepe.streamdeck.device.event.KeyEvent.Type.PRESSED;
import static de.acepe.streamdeck.device.event.KeyEvent.Type.RELEASED;

public class DeckManager {

    private static final Page EMPTY_PAGE = new Page("empty");
    private final HashMap<UUID, Page> pages = new HashMap<>();
    private final StreamDeckDevices streamDeckDevices;

    private Consumer<Integer> uiCallback;
    private Page currentPage;

    @Inject
    DeckManager(StreamDeckDevices streamDeckDevices) {
        this.streamDeckDevices = streamDeckDevices;
        registerPage(EMPTY_PAGE);
        setCurrentPage(EMPTY_PAGE.getId());
        streamDeckDevices.registerDecksDiscoveredCallback(this::onStreamdeckConnected);
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

    public void setCurrentPage(UUID pageId) {
        if (currentPage != null) {
            currentPage.unbindDeckManager();
        }

        currentPage = pages.get(pageId);
        currentPage.bindDeckManager(this);
        currentPage.update();
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
}
