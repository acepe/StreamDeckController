package de.acepe.streamdeck.backend.behaviours;

import de.acepe.streamdeck.backend.DeckManager;
import de.acepe.streamdeck.device.IStreamDeck;
import de.acepe.streamdeck.device.event.KeyEvent;

import javax.inject.Inject;

public class StreamDeckToggleBrightnessBehavior extends BasicButtonBehaviour {

    private final DeckManager deckManager;

    @Inject
    public StreamDeckToggleBrightnessBehavior(DeckManager deckManager) {
        this.deckManager = deckManager;
    }

    @Override
    public void onButtonPressed(KeyEvent event) {
    }

    @Override
    public void onButtonReleased(KeyEvent event) {
        IStreamDeck deck = deckManager.getDeck();
        int brightness = deck.getBrightness();
        if (brightness >= 99) {
            brightness = 0;
        }
        brightness = brightness - brightness % 25 + 25;
        deck.setBrightness(brightness);
    }
}
