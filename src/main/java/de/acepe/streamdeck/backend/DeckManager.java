package de.acepe.streamdeck.backend;

import de.acepe.streamdeck.backend.config.Page;
import de.acepe.streamdeck.device.IStreamDeck;
import de.acepe.streamdeck.device.event.KeyEvent;

import javax.inject.Inject;
import java.util.function.Consumer;

import static de.acepe.streamdeck.device.event.KeyEvent.Type.PRESSED;
import static de.acepe.streamdeck.device.event.KeyEvent.Type.RELEASED;

public class DeckManager {

    private final IStreamDeck deck;

    private Consumer<Integer> uiCallback;
    private Page currentPage = new Page("empty");

    @Inject
    DeckManager(IStreamDeck deck) {
        this.deck = deck;
        deck.reset();
        deck.setLogo();
        deck.addKeyListener(this::onDeckEventsReceived);
    }

    public void bindUICallback(Consumer<Integer> uiCallback) {
        this.uiCallback = uiCallback;
        currentPage.bindDeckManager(deck, uiCallback);
    }

    public void setCurrentPage(Page page) {
        currentPage.unbindDeckManager();
        currentPage = page;
        currentPage.bindDeckManager(deck, uiCallback);
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
        currentPage.fireActionFromUI(index, deck);
    }

    public void stop() {
        deck.dispose();
        currentPage.unbindDeckManager();
    }

    public Page getCurrentPage() {
        return currentPage;
    }

}
