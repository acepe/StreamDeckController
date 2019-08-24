package de.acepe.streamdeck.ui;

import de.acepe.streamdeck.app.ControlledScreen;
import de.acepe.streamdeck.app.ScreenManager;
import de.acepe.streamdeck.app.Screens;
import de.acepe.streamdeck.backend.DeckManager;
import de.acepe.streamdeck.backend.config.ExamplePageFactory;
import de.acepe.streamdeck.backend.config.Persistence;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.stream.IntStream;

import static de.acepe.streamdeck.backend.config.ExamplePageFactory.PAGE_1_ID;
import static de.acepe.streamdeck.backend.config.ExamplePageFactory.PAGE_2_ID;

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
    public MainViewController(ScreenManager screenManager,
                              DeckManager deckManager,
                              ExamplePageFactory examplePageFactory,
                              Persistence persistence) {
        this.screenManager = screenManager;
        this.deckManager = deckManager;
        deckManager.bindUpdateUICallback(this::updateButtonUI);

        deckManager.loadPage(PAGE_2_ID);
        deckManager.loadPage(PAGE_1_ID);

//        Page page2 = examplePageFactory.configurePage2();
//        persistence.savePage(page2);
//
//        Page page1 = examplePageFactory.configurePage1();
//        persistence.savePage(page1);
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

        deckManager.setCurrentPage(PAGE_1_ID);
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
        ((ImageView) button.getGraphic()).setImage(deckManager.getButtonImage(index));
    }

    @FXML
    void onSettingsPerformed() {
        screenManager.setScreen(Screens.SETTINGS, ScreenManager.Direction.LEFT);
    }

}
