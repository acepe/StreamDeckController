package de.acepe.streamdeck.backend;

import de.acepe.streamdeck.device.event.KeyEvent;

public interface ButtonBehaviour {

    void bindButton(DeckButton deckButton);

    void unbindButton(DeckButton deckButton);

    void onButtonPressed(KeyEvent event);

    void onButtonReleased(KeyEvent event);

}
