package de.acepe.streamdeck.backend.config.behaviours;

import de.acepe.streamdeck.backend.DeckManager;
import de.acepe.streamdeck.backend.config.DeckButton;
import de.acepe.streamdeck.device.event.KeyEvent;

public interface ButtonBehaviour {

    void bindButton(DeckButton deckButton);

    void unbindButton(DeckButton deckButton);

    void onButtonPressed(KeyEvent event);

    void onButtonReleased(KeyEvent event);

    void bindDeckManager(DeckManager deckManager);

    void unbindDeckManager();

}
