package de.acepe.streamdeck.ui;

import de.acepe.streamdeck.app.ControlledScreen;
import de.acepe.streamdeck.app.ScreenManager;
import de.acepe.streamdeck.app.Screens;
import de.acepe.streamdeck.backend.DeckManager;
import de.acepe.streamdeck.device.StreamDeck;
import de.acepe.streamdeck.device.StreamDeckDevices;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.inject.Inject;

public class SettingsController implements ControlledScreen {

    private final ScreenManager screenManager;
    private final StreamDeck streamDeck;
    private final DeckManager deckManager;

    @FXML
    private Label settingsLabel;
    @FXML
    private Button backButton;
    @FXML
    private Button connectButton;

    @Inject
    public SettingsController(ScreenManager screenManager, StreamDeck streamDeck, DeckManager deckManager) {
        this.screenManager = screenManager;
        this.streamDeck = streamDeck;
        this.deckManager = deckManager;
    }

    @FXML
    private void initialize() {
        GlyphsDude.setIcon(backButton, FontAwesomeIcon.CHEVRON_LEFT, "1.5em");

    }

    @FXML
    private void onSaveSettingsPerformed() {
        screenManager.setScreen(Screens.MAIN, ScreenManager.Direction.RIGHT);
    }

    @FXML
    private void onConnectPerformed() {
        streamDeck.reconnect();
        deckManager.setCurrentPage(deckManager.getCurrentPage().getId());
    }
}
