package de.acepe.streamdeck.backend.behaviours;

import de.acepe.streamdeck.backend.DeckManager;
import de.acepe.streamdeck.device.event.KeyEvent;

import javax.inject.Inject;
import java.util.UUID;

public class ShowPageBehaviour extends BasicButtonBehaviour {

    private final DeckManager deckManager;
    private UUID pageId;

    @Inject
    public ShowPageBehaviour(DeckManager deckManager) {
        this.deckManager = deckManager;
    }

    @Override
    public void onButtonPressed(KeyEvent event) {
    }

    @Override
    public void onButtonReleased(KeyEvent event) {
        deckManager.setCurrentPage(pageId);
    }

    public void setPageId(UUID pageId) {
        this.pageId = pageId;
    }

    public UUID getPageId() {
        return pageId;
    }
}
