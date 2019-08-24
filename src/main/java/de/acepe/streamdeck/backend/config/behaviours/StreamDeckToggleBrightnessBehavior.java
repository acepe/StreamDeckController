package de.acepe.streamdeck.backend.config.behaviours;

import de.acepe.streamdeck.device.IStreamDeck;
import de.acepe.streamdeck.device.event.KeyEvent;

public class StreamDeckToggleBrightnessBehavior extends BasicButtonBehaviour {

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
