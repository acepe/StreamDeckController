package de.acepe.streamdeck.backend.config.behaviours;

import de.acepe.streamdeck.backend.DeckManager;
import de.acepe.streamdeck.backend.config.DeckButton;

public abstract class BasicButtonBehaviour implements ButtonBehaviour {

    protected DeckManager deckManager;

    @Override
    public void bindButton(DeckButton deckButton) {
        //NOP
    }

    @Override
    public void unbindButton(DeckButton deckButton) {
        //NOP
    }

    public void bindDeckManager(DeckManager deckManager) {
        this.deckManager = deckManager;
    }

    @Override
    public void unbindDeckManager() {
        this.deckManager = null;
    }
}
