package de.acepe.streamdeck.ui;

import de.acepe.streamdeck.app.ControlledScreen;
import de.acepe.streamdeck.app.ScreenManager;
import de.acepe.streamdeck.app.Screens;
import de.acepe.streamdeck.backend.DeckButton;
import de.acepe.streamdeck.backend.DeckManager;
import de.acepe.streamdeck.backend.behaviours.*;
import de.acepe.streamdeck.backend.config.Page;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.stream.IntStream;

import static java.awt.event.KeyEvent.*;

public class MainViewController implements ControlledScreen {

    private final ArrayList<Button> buttons = new ArrayList<>(15);
    private final ScreenManager screenManager;
    private final DeckManager deckManager;
    private final Page page;

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
                              Provider<ExecuteProgrammBehaviour> executeProgramm,
                              Provider<OpenLocationBehaviour> openLocation,
                              Provider<SleepBehaviour> sleepBehaviour) {
        this.screenManager = screenManager;
        this.deckManager = deckManager;
        deckManager.bindUICallback(this::updateButtonUI);

        page = createPage(toggleBrightness, hotkey, executeProgramm, openLocation, sleepBehaviour);
    }

    private Page createPage(Provider<StreamDeckToggleBrightnessBehavior> toggleBrightness, Provider<HotKeyBehaviour> hotkey, Provider<ExecuteProgrammBehaviour> executeProgramm, Provider<OpenLocationBehaviour> openLocation, Provider<SleepBehaviour> sleepBehaviour) {
        Page page = new Page("First Page");

        IntStream.rangeClosed(0, 10).forEach(i -> page.addButton(i, new DeckButton(String.valueOf(i))));
        IntStream.rangeClosed(11, 14).forEach(i -> page.addButton(i, new DeckButton("D" + (15 - i))));

        DeckButton brightnessButton = page.getButton(0);
        brightnessButton.setText("Br");
        brightnessButton.addBehaviour(toggleBrightness.get());

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

        page.getButton(14).addBehaviour(vdesk1);
        page.getButton(13).addBehaviour(vdesk2);
        page.getButton(12).addBehaviour(vdesk3);
        page.getButton(11).addBehaviour(vdesk4);

        OpenLocationBehaviour openGoogle = openLocation.get();
        openGoogle.setUri("https://google.de");
        DeckButton openGoogleButton = page.getButton(9);
        openGoogleButton.setText("Google", 20);
        openGoogleButton.addBehaviour(openGoogle);

        OpenLocationBehaviour openHome = openLocation.get();
        openHome.setFile(System.getProperty("user.home"));
        DeckButton openHomeButton = page.getButton(7);
        openHomeButton.setText("⌂", 60);
        openHomeButton.addBehaviour(openHome);

        ExecuteProgrammBehaviour startChrome = executeProgramm.get();
        startChrome.setProgramm("/usr/bin/google-chrome");
        startChrome.setArguments("--incognito");
        DeckButton chromeButton = page.getButton(8);
        chromeButton.setText("Chrome", 20);
        chromeButton.addBehaviour(startChrome);

        SleepBehaviour sleep = sleepBehaviour.get();
        sleep.setDurationInMillis(2000);
        HotKeyBehaviour vdesk1comp = hotkey.get();
        vdesk1comp.setKey(VK_F1);
        vdesk1comp.setModifier1(VK_CONTROL);
        OpenLocationBehaviour openHomeComp = openLocation.get();
        openHomeComp.setFile(System.getProperty("user.home"));
        DeckButton compoundButton = page.getButton(10);
        compoundButton.setText("Multi", 30);
        compoundButton.addBehaviour(vdesk1comp, sleep, openHomeComp);

        return page;
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

        configureButtons();

        deckManager.setCurrentPage(page);
    }

    private void configureButtons() {
        IntStream.rangeClosed(0, 14).forEach(i -> {
            Button button = buttons.get(i);
            button.setGraphic(new ImageView());
            button.setOnAction(event -> deckManager.fireActionFromUI(i));
        });
    }

    private void updateButtonUI(int index) {
        Button button = buttons.get(index);
        DeckButton deckButton = deckManager.getCurrentPage().getButton(index);
        ((ImageView) button.getGraphic()).setImage(deckButton.getImage());
    }

    @FXML
    void onSettingsPerformed() {
        screenManager.setScreen(Screens.SETTINGS, ScreenManager.Direction.LEFT);
    }

}
