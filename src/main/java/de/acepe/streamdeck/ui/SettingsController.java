package de.acepe.streamdeck.ui;

import de.acepe.streamdeck.app.ControlledScreen;
import de.acepe.streamdeck.app.ScreenManager;
import de.acepe.streamdeck.app.Screens;
import de.acepe.streamdeck.device.IStreamDeck;
import de.acepe.streamdeck.device.StreamDeckDevices;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.inject.Inject;

public class SettingsController implements ControlledScreen {

    private final ScreenManager screenManager;
    private final StreamDeckDevices streamDeckDevices;

    @FXML
    private TextField deviceName;
    @FXML
    private TextField serialNumber;
    @FXML
    private Button backButton;

    @Inject
    public SettingsController(ScreenManager screenManager, StreamDeckDevices streamDeckDevices) {
        this.screenManager = screenManager;
        this.streamDeckDevices = streamDeckDevices;
    }

    @FXML
    private void initialize() {
        GlyphsDude.setIcon(backButton, FontAwesomeIcon.CHEVRON_LEFT, "1.5em");
        streamDeckDevices.registerDecksDiscoveredCallback(this::setDeviceInfos);
        setDeviceInfos(streamDeckDevices.getStreamDeck());
    }

    private void setDeviceInfos(IStreamDeck deck) {
        deviceName.setText(deck.getDeviceName());
        serialNumber.setText(deck.getSerialnumber());
    }

    @FXML
    private void onSaveSettingsPerformed() {
        screenManager.setScreen(Screens.MAIN, ScreenManager.Direction.RIGHT);
    }

}
