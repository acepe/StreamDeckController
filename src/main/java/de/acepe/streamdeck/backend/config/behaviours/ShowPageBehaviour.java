package de.acepe.streamdeck.backend.config.behaviours;

import com.google.gson.annotations.Expose;
import de.acepe.streamdeck.device.event.KeyEvent;

public class ShowPageBehaviour extends BasicButtonBehaviour {

    @Expose
    private String pageId;

    @Override
    public void onButtonPressed(KeyEvent event) {
    }

    @Override
    public void onButtonReleased(KeyEvent event) {
        deckManager.setCurrentPage(pageId);
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getPageId() {
        return pageId;
    }
}
