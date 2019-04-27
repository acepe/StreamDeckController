package de.acepe.streamdeck.ui;

import de.acepe.streamdeck.app.ControlledScreen;
import de.acepe.streamdeck.app.ScreenManager;
import de.acepe.streamdeck.app.Screens;
import de.acepe.streamdeck.backend.ButtonBehaviour;
import de.acepe.streamdeck.backend.DeckButton;
import de.acepe.streamdeck.backend.DeckManager;
import de.acepe.streamdeck.backend.behaviours.HotKeyBehaviour;
import de.acepe.streamdeck.backend.behaviours.OpenFileBehaviour;
import de.acepe.streamdeck.backend.behaviours.StreamDeckToggleBrightnessBehavior;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.stream.IntStream;

import static java.awt.event.KeyEvent.*;

public class MainViewController implements ControlledScreen {

    private final ArrayList<Button> buttons = new ArrayList<>(15);
    private final ScreenManager screenManager;
    private final DeckManager deckManager;

    @FXML
    private Button button0;
    @FXML
    private Button button1;
    @FXML
    private Button button2;
    @FXML
    private Button button3;
    @FXML
    private Button button4;
    @FXML
    private Button button5;
    @FXML
    private Button button6;
    @FXML
    private Button button7;
    @FXML
    private Button button8;
    @FXML
    private Button button9;
    @FXML
    private Button button10;
    @FXML
    private Button button11;
    @FXML
    private Button button12;
    @FXML
    private Button button13;
    @FXML
    private Button button14;

    @FXML
    private Button settingsButton;

    @Inject
    public MainViewController(ScreenManager screenManager, DeckManager deckManager,
                              Provider<StreamDeckToggleBrightnessBehavior> toggleBrightness,
                              Provider<HotKeyBehaviour> hotkey,
                              Provider<OpenFileBehaviour> openFile) {
        this.screenManager = screenManager;
        this.deckManager = deckManager;

        IntStream.rangeClosed(0, 10).forEach(i -> deckManager.addButton(i, new DeckButton(String.valueOf(i))));
        IntStream.rangeClosed(11, 14).forEach(i -> deckManager.addButton(i, new DeckButton("D" + (15 - i))));
        deckManager.getButton(0).addBehaviour(toggleBrightness.get());

        HotKeyBehaviour vdesk1 = hotkey.get();
        vdesk1.setKey(VK_F1);
        vdesk1.setModifier1(VK_CONTROL);

        HotKeyBehaviour vdesk2 = hotkey.get();
        vdesk2.setKey(VK_F2);
        vdesk2.setModifier1(VK_CONTROL);

        HotKeyBehaviour vdesk3 = hotkey.get();
        vdesk3.setKey(VK_F3);
        vdesk3.setModifier1(VK_CONTROL);

        HotKeyBehaviour vdesk4 = hotkey.get();
        vdesk4.setKey(VK_F4);
        vdesk4.setModifier1(VK_CONTROL);

        deckManager.getButton(14).addBehaviour(vdesk1);
        deckManager.getButton(13).addBehaviour(vdesk2);
        deckManager.getButton(12).addBehaviour(vdesk3);
        deckManager.getButton(11).addBehaviour(vdesk4);

        OpenFileBehaviour startChrome = openFile.get();
        try {
            startChrome.setUri(new URI("https://google.de"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        deckManager.getButton(9).addBehaviour(startChrome);
        deckManager.updateDeck();
    }

    @FXML
    private void initialize() {
        GlyphsDude.setIcon(settingsButton, FontAwesomeIcon.COG, "1.5em");
        buttons.add(button0);
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        buttons.add(button4);
        buttons.add(button5);
        buttons.add(button6);
        buttons.add(button7);
        buttons.add(button8);
        buttons.add(button9);
        buttons.add(button10);
        buttons.add(button11);
        buttons.add(button12);
        buttons.add(button13);
        buttons.add(button14);

        bindDeckButtons();
    }

    private void bindDeckButtons() {
        IntStream.rangeClosed(0, 14).filter(i -> deckManager.getButton(i) != null)
                 .forEach(this::bindDeckButton);
    }

    private void bindDeckButton(int index) {
        Button button = buttons.get(index);
        DeckButton deckButton = deckManager.getButton(index);
        button.setGraphic(new ImageView(deckButton.getImage()));
        button.setOnAction(event -> deckManager.fireActionFromUI(index));
    }

    @FXML
    void onSettingsPerformed() {
        screenManager.setScreen(Screens.SETTINGS, ScreenManager.Direction.LEFT);
    }

}
