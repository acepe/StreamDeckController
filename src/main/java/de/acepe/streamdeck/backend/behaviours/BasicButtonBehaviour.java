package de.acepe.streamdeck.backend.behaviours;

import de.acepe.streamdeck.backend.ButtonBehaviour;
import de.acepe.streamdeck.backend.DeckButton;

public abstract class BasicButtonBehaviour implements ButtonBehaviour {
    @Override
    public void bindButton(DeckButton deckButton) {
        //NOP
    }

    @Override
    public void unbindButton(DeckButton deckButton) {
        //NOP
    }
}
