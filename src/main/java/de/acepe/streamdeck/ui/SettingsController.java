package de.acepe.streamdeck.ui;

import de.acepe.streamdeck.app.ControlledScreen;
import de.acepe.streamdeck.app.ScreenManager;
import de.acepe.streamdeck.app.Screens;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.inject.Inject;

public class SettingsController implements ControlledScreen {

    private final ScreenManager screenManager;

    @FXML
    private Label settingsLabel;
    @FXML
    private Button backButton;

    @Inject
    public SettingsController(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

    @FXML
    private void initialize() {
        GlyphsDude.setIcon(backButton, FontAwesomeIcon.CHEVRON_LEFT, "1.5em");

    }

    @FXML
    void onSaveSettingsPerformed() {
        screenManager.setScreen(Screens.MAIN, ScreenManager.Direction.RIGHT);
    }

}
